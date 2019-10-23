package com.ms.cse.dqprofileapp.cloudfunctions;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ms.cse.dqprofileapp.models.*;
import kong.unirest.JsonNode;

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

    @Autowired
    private RulesInfoRepository rulesInfoRepository;

    @Bean
    public Function<FunctionInput, List<RulesInfo>> getLatestDQRules() {
        return input -> {
            Logger logger = input.getExecutionContext().getLogger();
            QnsHttpClient qnsClient = QnsHttpClient.getInstance(this.qnsSvcUrl, this.qnsSvcCode, logger);
            JsonCreatorHttpClient jsonCreatorClient = JsonCreatorHttpClient.getInstance(this.jsonCreatorSvcUrl, this.jsonCreatorSvcCode, logger);

            //List<RulesInfo> rulesInfo = rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            List<RulesInfo> rulesInfo = (List) rulesInfoRepository.findAll();
            List<JsonWrapperEntity> entities = new ArrayList<JsonWrapperEntity>();
            
            for (RulesInfo ruleInfo : rulesInfo) {                
                //"storageuri/filesystemname/f1/f2/f3";
                //"rule1";

                QualifiedNameServiceResponse qualifiedNameResponse = qnsClient.getQualifiedName(ruleInfo.getColumnFQDN(), ruleInfo.getRuleId());

                if (!qualifiedNameResponse.isExists()){
                    JsonWrapperEntity entity = JsonWrapperEntity.create(ruleInfo);
                    entities.add(entity);
                }
            }

            if(entities.size() > 0) {
                JsonNode json = jsonCreatorClient.getJson(JsonWrapperEntity.from(entities));
                System.out.println(json.toPrettyString());
            }
            return rulesInfo;
        };
    }
}