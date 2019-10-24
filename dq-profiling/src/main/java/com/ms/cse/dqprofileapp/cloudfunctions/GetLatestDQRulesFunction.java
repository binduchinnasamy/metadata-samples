package com.ms.cse.dqprofileapp.cloudfunctions;

import java.util.UUID;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ms.cse.dqprofileapp.models.*;
import kong.unirest.JsonNode;
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

            //List<RulesInfo> rulesInfo = rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            List<RulesInfo> rulesInfo = (List<RulesInfo>) rulesInfoRepository.findAll();
            List<JsonWrapperEntity> entities = new ArrayList<JsonWrapperEntity>();
            
            for (RulesInfo ruleInfo : rulesInfo) {                
                QualifiedNameServiceResponse qualifiedNameResponse = qnsClient.getQualifiedName(ruleInfo.getColumnFQDN(), ruleInfo.getRuleId());
                System.out.println("qualifiedNameResponse:" + qualifiedNameResponse.toString());
                                
                if (!qualifiedNameResponse.isExists()){
                    JsonWrapperEntity entity = JsonWrapperEntity.create(ruleInfo);
                    entities.add(entity);
                }
            }

            if(entities.size() > 0) {
                JsonCreatorHttpClient jsonCreatorClient = JsonCreatorHttpClient.getInstance(this.jsonCreatorSvcUrl, this.jsonCreatorSvcCode, logger);
                AtlasWrapperHttpClient atlasWrapperClient = AtlasWrapperHttpClient.getInstance(this.atlasWrapperSvcUrl, logger);
                
                JsonNode jsonWrapperEntities = jsonCreatorClient.getJson(JsonWrapperEntity.from(entities));
                System.out.println("jsonWrapperEntities:" + jsonWrapperEntities.toPrettyString());
                
                //take output json and call atlaswrapper create bulk entity
                MutatedEntities mutatedEntities = atlasWrapperClient.createBulk(jsonWrapperEntities);
                System.out.println("mutatedEntities: " + mutatedEntities.toString());

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

                //extract guids of created entities from mutatedEntities.guidAssignments
                List<UUID> guids = new ArrayList<UUID>();
                for (MutatedEntity mutatedEntity : mutatedEntities.getCREATE()) {
                    guids.add(mutatedEntity.getGuid());
                }

                System.out.println("guids: " + guids.toString());

                //call atlaswrapper t0 get fqdn of the columnt entity from original rulesInfo

            }

            return rulesInfo;
        };
    }
}