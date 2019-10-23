package com.ms.cse.dqprofileapp.cloudfunctions;

import java.util.logging.Logger;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

import com.ms.cse.dqprofileapp.clients.*;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.FunctionInput;
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
    
    @Autowired
    private RulesInfoRepository rulesInfoRepository;

    @Bean
    public Function<FunctionInput, List<RulesInfo>> getLatestDQRules() {
        return input -> {
            List<RulesInfo> rulesInfo = rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            
            Logger logger = null;
            String url = qnsSvcUrl + "?code=" + qnsSvcCode;

            QnsHttpClient qnsClient = QnsHttpClient.getInstance(url);
            qnsClient.getQualifiedName();

            return rulesInfo;
        };
    }
}