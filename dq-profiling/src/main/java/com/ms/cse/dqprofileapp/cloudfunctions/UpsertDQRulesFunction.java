package com.ms.cse.dqprofileapp.cloudfunctions;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;
import java.util.function.Function;

import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
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
public class UpsertDQRulesFunction {

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

    @Value("${dqProfileApp.upsertDQRules.queryAll}")
    private boolean queryAll;

    @Autowired
    private RulesInfoRepository rulesInfoRepository;

    @Bean
    public Function<FunctionInput, Integer> upsertDQRules() {
        return input -> {
            Logger logger = input.getExecutionContext().getLogger();

            QnsHttpClient qnsClient = QnsHttpClient.getInstance(this.qnsSvcUrl, this.qnsSvcCode, logger);
            JsonCreatorHttpClient jsonCreatorClient = JsonCreatorHttpClient.getInstance(this.jsonCreatorSvcUrl, this.jsonCreatorSvcCode, logger);
            AtlasWrapperHttpClient atlasWrapperClient = AtlasWrapperHttpClient.getInstance(this.atlasWrapperSvcUrl, logger);

            List<RulesInfo> rulesInfo = queryRulesInfo(input.getTimeStamp());
            List<JsonWrapperEntity> entities = new ArrayList<JsonWrapperEntity>();

            try {

                for (RulesInfo ruleInfo : rulesInfo) {
                    entities.clear();
                    String columnFqdn = ruleInfo.getColumnFQDN();
                    QualifiedNameServiceResponse qualifiedNameResponse = qnsClient.getQualifiedName(columnFqdn, ruleInfo.getRuleId());

                    UUID ruleIdUUID;
                    if (!qualifiedNameResponse.isExists()) { //rule doesn't exist, so go create it
                        JsonWrapperEntity entity = JsonWrapperEntity.create(ruleInfo, qualifiedNameResponse.getQualifiedName());
                        entities.add(entity);

                        JsonNode jsonWrapperEntities = jsonCreatorClient.getJson(JsonWrapperEntity.from(entities));

                        //take output json and call atlaswrapper create bulk entity
                        JsonNode mutatedEntities = atlasWrapperClient.createBulk(jsonWrapperEntities);
                        String uuid = mutatedEntities.getObject().getJSONObject("mutatedEntities").getJSONArray("CREATE").getJSONObject(0).getString("guid");
                        ruleIdUUID = UUID.fromString(uuid);
                    } else { //rule already existed
                        ruleIdUUID = qualifiedNameResponse.getGuid();
                    }

                    //call atlas to find the column entity
                    String searchCriteria = "column+where+qualifiedName=" + columnFqdn;

                    JsonNode searchResult = atlasWrapperClient.search(searchCriteria);
                    for (Object obj : searchResult.getObject().getJSONArray("entities")){
                        JSONObject entity = (JSONObject)obj;

                        String typeName = entity.getString("typeName");
                        String qualifiedName = entity.getJSONObject("attributes").getString("qualifiedName");

                        if (typeName.equalsIgnoreCase("column") && qualifiedName.equalsIgnoreCase(columnFqdn)){
                            String entityId = entity.getString("guid");

                            //get atlas entity using its guid
                            JsonNode colEntity = atlasWrapperClient.getEntity(entityId);

                            colEntity = PrepareColumnWithRules(colEntity, qualifiedNameResponse.getQualifiedName(), ruleInfo.getRuleId(), ruleIdUUID.toString());
                            atlasWrapperClient.upsertEntity(colEntity);
                        }
                    }
                }
            } catch (Exception e)
            {
                logger.info("exception: " + e.toString());
            }

            return 0;
        };
    }

    private JsonNode PrepareColumnWithRules(JsonNode colEntity, String qualifiedName, String ruleId, String ruleIdUUID) {
        JSONArray dqRules = getDQRulesJSONArray(colEntity.getObject().getJSONObject("entity").getJSONObject("attributes"));

        JSONObject dqRuleAttributes = new JSONObject();
        dqRuleAttributes.put("qualifiedName", qualifiedName);
        dqRuleAttributes.put("rule_id", ruleId);

        JSONObject dqRule = new JSONObject();
        dqRule.put("guid", ruleIdUUID);
        dqRule.put("typeName", "dq_rule");
        dqRule.put("uniqueAttributes", dqRuleAttributes);

        boolean dqRuleExits = false;
        for (Object obj : dqRules) {
            //dq_rules with guid exists
            if (((JSONObject) obj).getString("guid").equalsIgnoreCase(ruleIdUUID)) {
                dqRuleExits = true;
                break;
            }
        }
        //dq_rules with no match
        if (!dqRuleExits) {
            dqRules.put(dqRule);
        }

        colEntity.getObject().getJSONObject("entity").getJSONObject("attributes").remove("dq_rules");
        colEntity.getObject().getJSONObject("entity").getJSONObject("attributes").put("dq_rules", dqRules);
        return colEntity;
    }

    private JSONArray getDQRulesJSONArray(JSONObject attributes) {
        if(attributes.has("dq_rules")) {
            return attributes.getJSONArray("dq_rules");
        }

        return new JSONArray();
    }

    private List<RulesInfo> queryRulesInfo(Timestamp waterMarkTimestamp) {
        if(queryAll) {
            return (List<RulesInfo>) rulesInfoRepository.findAll();
        }

        return rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(waterMarkTimestamp, TimestampExtension.now());
    }
}