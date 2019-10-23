package com.ms.cse.dqprofileapp.cloudfunctions;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import kong.unirest.JsonNode;

import com.google.gson.JsonObject;
import com.ms.cse.dqprofileapp.clients.*;
import com.ms.cse.dqprofileapp.models.FunctionInput;
import com.ms.cse.dqprofileapp.models.JsonWrapperEntity;
import com.ms.cse.dqprofileapp.models.JsonWrapperEntityAttribute;
import com.ms.cse.dqprofileapp.models.RulesInfo;
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
    private String jsoncreatorSvcUrl;

    @Value("${dqProfileApp.jsoncreator-svc.code}")
    private String jsoncreatorSvcCode;

    @Autowired
    private RulesInfoRepository rulesInfoRepository;

    @Bean
    public Function<FunctionInput, List<RulesInfo>> getLatestDQRules() {
        return input -> {
            Logger logger = input.getExecutionContext().getLogger();
            String url = qnsSvcUrl + "?code=" + qnsSvcCode;
            
            //List<RulesInfo> rulesInfo = rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            List<RulesInfo> rulesInfo = (List) rulesInfoRepository.findAll();
            
            for (RulesInfo ruleInfo : rulesInfo) {                
                String colQualigiedName = ruleInfo.getColumnFQDN(); //"storageuri/filesystemname/f1/f2/f3";
                String ruleId = ruleInfo.getRuleId(); //"rule1";
                String typeName = "dq_rule";

                QnsHttpClient qnsClient = QnsHttpClient.getInstance(url, logger);
                JsonNode qualifiedNameResponse = qnsClient.getQualifiedName(colQualigiedName, ruleId); 
                
                if (true){
                    url = jsoncreatorSvcUrl + "?code=" + jsoncreatorSvcCode; 
                    JsonCreatorHttpClient jsoncreatorClient = JsonCreatorHttpClient.getInstance(url, logger);
                    
                    /*	
                    "entity_type_name": "dq_rule", 
                    "attributes": [
                        {"attr_name": "rule_id", "attr_value": "rule1"}, 
                        {"attr_name": "name", "attr_value": "rule1" }, 
                        {"attr_name": "qualifiedName", "attr_value": "storageuri/filesystemname/f1/f2/f3/rule1"}
                    ],
                    "created_by": "dqrules_harvester"
                    */
                    
                    List<JsonWrapperEntityAttribute> attributes = new ArrayList<JsonWrapperEntityAttribute>();
                    JsonWrapperEntityAttribute jwea = new JsonWrapperEntityAttribute();
                    jwea.setAttr_name("rule_id"); jwea.setAttr_value("rule1");
                    attributes.add(jwea);
                    
                    jwea = new JsonWrapperEntityAttribute();
                    jwea.setAttr_name("name"); jwea.setAttr_value("rule1");
                    attributes.add(jwea);

                    jwea = new JsonWrapperEntityAttribute();
                    jwea.setAttr_name("qualifiedName"); jwea.setAttr_value("storageuri/filesystemname/f1/f2/f3/rule1");
                    attributes.add(jwea);

                    JsonWrapperEntityAttribute[] arrjwea = new JsonWrapperEntityAttribute[attributes.size()];
                    arrjwea = attributes.toArray(arrjwea);
                    
                    JsonWrapperEntity jwe = new JsonWrapperEntity();
                    jwe.setEntity_type_name(typeName);
                    jwe.setAttributes(arrjwea);
                    jwe.setCreated_by("dqrule_profiler");

                    List<JsonWrapperEntity> entities = new ArrayList<JsonWrapperEntity>();
                    entities.add(jwe);

                    JsonWrapperEntity[] arrjwe = new JsonWrapperEntity[1];
                    arrjwe = entities.toArray(arrjwe);
                    
                    JsonNode retval = jsoncreatorClient.getJson(arrjwe);
                    
                    System.out.println(retval.toPrettyString());
                }
            }
            
            return rulesInfo;
        };
    }
}