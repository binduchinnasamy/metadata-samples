package com.ms.cse.dqprofileapp.cloudfunctions;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import com.microsoft.azure.functions.ExecutionContext;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.FunctionInput;
import com.ms.cse.dqprofileapp.models.RulesInfo;
import com.ms.cse.dqprofileapp.repositories.RulesInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GetLatestDQRulesFunction {
    @Autowired
    private RulesInfoRepository rulesInfoRepository;

    @Bean
    public Function<FunctionInput, List<RulesInfo>> getLatestDQRules() {
        return input -> {
            List<RulesInfo> rulesInfo = rulesInfoRepository.findByUpdateTimestampBetweenOrderByUpdateTimestampDesc(input.getTimeStamp(), TimestampExtension.now());
            return rulesInfo;
        };
    }
}