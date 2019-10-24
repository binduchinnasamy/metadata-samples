package com.ms.cse.dqprofileapp.cloudfunctions;

import java.util.*;
import java.util.logging.Logger;
import java.util.function.Function;

import com.ms.cse.dqprofileapp.models.*;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import com.ms.cse.dqprofileapp.clients.*;
import com.ms.cse.dqprofileapp.repositories.RulesInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GetLatestDQRulesFunction {

    @Value("${dqProfileApp.qns-svc.baseUrl}")
    private String qnsSvcUrl;

    @Value("${dqProfileApp.qns-svc.code}")
    private String qnsSvcCode;
    
    @Value("${dqProfileApp.jsoncreator-svc.baseUrl}")
    private String jsonCreatorSvcUrl;

    @Value("${dqProfileApp.jsoncreator-svc.code}")
    private String jsonCreatorSvcCode;

    @Value("${dqProfileApp.atlaswrapper-svc.baseUrl}")
    private String atlasWrapperSvcUrl;

    @Autowired
    private RulesInfoRepository rulesInfoRepository;

    @Bean
    public Function<FunctionInput, List<RulesInfo>> getLatestDQRules() {
        return input -> {
            Logger logger = input.getExecutionContext().getLogger();

            QnsHttpClient qnsClient = QnsHttpClient.getInstance(this.qnsSvcUrl, this.qnsSvcCode, logger);
            JsonCreatorHttpClient jsonCreatorClient = JsonCreatorHttpClient.getInstance(this.jsonCreatorSvcUrl, this.jsonCreatorSvcCode, logger);
            AtlasWrapperHttpClient atlasWrapperClient = AtlasWrapperHttpClient.getInstance(this.atlasWrapperSvcUrl, logger);

            Dictionary colfqnDictionary = new Hashtable();

            //List<RulesInfo> rulesInfo = rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            List<RulesInfo> rulesInfo = (List<RulesInfo>) rulesInfoRepository.findAll();
            List<JsonWrapperEntity> entities = new ArrayList<JsonWrapperEntity>();

            try {

                for (RulesInfo ruleInfo : rulesInfo) {
                    entities.clear();

                    colfqnDictionary.put(ruleInfo.getColumnFQDN(), new ArrayList<String>());

                    QualifiedNameServiceResponse qualifiedNameResponse = qnsClient.getQualifiedName(ruleInfo.getColumnFQDN(), ruleInfo.getRuleId());
                    System.out.println("qualifiedNameResponse:" + qualifiedNameResponse.toString());
                    UUID ruleIdUUID;
                    if (!qualifiedNameResponse.isExists()) { //rule doesn't exist, so go create it
                        JsonWrapperEntity entity = JsonWrapperEntity.create(ruleInfo, qualifiedNameResponse.getQualifiedName());
                        entities.add(entity);

                        JsonNode jsonWrapperEntities = jsonCreatorClient.getJson(JsonWrapperEntity.from(entities));
                        System.out.println("jsonWrapperEntities:" + jsonWrapperEntities.toPrettyString());

                        //take output json and call atlaswrapper create bulk entity
                        JsonNode mutatedEntities = atlasWrapperClient.create(jsonWrapperEntities);
                        String uuid = mutatedEntities.getObject().getJSONObject("mutatedEntities").getJSONArray("CREATE").getJSONObject(0).getString("guid");

                        System.out.println("uuid: " + uuid);

                        //TODO: throw if mutatedEntities == null

                    /*
                    {
                        "mutatedEntities": {
                            "CREATE": [{
                                "typeName": "dq_rule",
                                "attributes": {
                                    "rule_id": "rule1"
                                },
                                "guid": "0a3ea624-6bea-4322-bc49-92942d1297f1"
                            }, {
                                "typeName": "dq_rule",
                                "attributes": {
                                    "rule_id": "rule2"
                                },
                                "guid": "07f09b27-f38f-4b5c-bec0-af7b54acccb3"
                            }]
                        },
                        "guidAssignments": {
                            "-77653153359712": "07f09b27-f38f-4b5c-bec0-af7b54acccb3",
                            "-77653153359711": "0a3ea624-6bea-4322-bc49-92942d1297f1"
                        }
                    }
                    */

                        ruleIdUUID = UUID.fromString(uuid);
                    } else { //rule already existed
                        ruleIdUUID = qualifiedNameResponse.getGuid();
                    }

                    //cally atlas to find the column entit
                    String searchCriteria = "column+where+qualifiedName=" + ruleInfo.getColumnFQDN();

                    JsonNode searchResult = atlasWrapperClient.search(searchCriteria);
                    for (Object obj : searchResult.getObject().getJSONArray("entities")){
                        JSONObject entity = (JSONObject)obj;

                        String typeName = entity.getString("typeName");
                        String qualifiedName = entity.getJSONObject("attributes").getString("qualifiedName");

                        if (typeName.equalsIgnoreCase("column") && qualifiedName.equalsIgnoreCase(ruleInfo.getColumnFQDN())){
                            String entityId = entity.getString("guid");

                            //get atlas entity using its guid
                            JsonNode colEntity = atlasWrapperClient.getEntity(entityId);

                            //enity.attriburtes.dq_rules[] null or empty
                            JSONArray dqRules = colEntity.getObject().getJSONObject("entity").getJSONObject("attributes").getJSONArray("dq_rules");

                            RuleAttribute ra = new RuleAttribute();
                            ra.setQualifiedName(qualifiedNameResponse.getQualifiedName());
                            ra.setRule_id(ruleInfo.getRuleId());

                            Rule r = new Rule();
                            r.setGuid(ruleIdUUID.toString());
                            r.setTypeName("dq_rule");
                            r.setUniqueAttributes(ra);

                            if ( dqRules == null || dqRules.isEmpty()) {
                                dqRules = new JSONArray();
                                dqRules.put(r);
                            }
                            else {
                                JSONArray dqrulesarr = colEntity.getObject().getJSONObject("entity").getJSONObject("attributes").getJSONArray("dq_rules");
                                Boolean dqruleFound = false;

                                for (Object oobj : dqrulesarr) {
                                    JSONObject dqrule = (JSONObject) oobj;

                                    //dq_rules with guid exists
                                    if (dqrule.getString("guid").equalsIgnoreCase(ruleIdUUID.toString())) {
                                        dqruleFound = true;
                                        break;
                                    }
                                }
                                //dq_rules with no match
                                if (!dqruleFound) {
                                    dqRules.put(r);
                                }
                            }

                            colEntity.getObject().getJSONObject("entity").getJSONObject("attributes").remove("dq_rules");
                            colEntity.getObject().getJSONObject("entity").getJSONObject("attributes").put("dq_rules", dqRules);

                        }
                    }
                }
            } catch (Exception e)
            {
                System.out.println("E:" + e.toString());
            }

            return rulesInfo;
        };
    }
}