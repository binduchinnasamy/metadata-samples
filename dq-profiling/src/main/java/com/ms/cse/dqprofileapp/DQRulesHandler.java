package com.ms.cse.dqprofileapp;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.RulesInfo;
import com.ms.cse.dqprofileapp.models.ScheduleStatus;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Timestamp;
import java.util.List;

@ComponentScan(basePackages={"com.ms.cse.dqprofileapp"})
public class DQRulesHandler extends AzureSpringBootRequestHandler<Timestamp, List<RulesInfo>> {

    @FunctionName("getLatestDQRules")
    public List<RulesInfo> execute(
            @TimerTrigger(name = "getLatestDQRulesTrigger", schedule = "0 */2 * * * *") String timerInfo,
            ExecutionContext context) {

        ScheduleStatus scheduleStatus = ScheduleStatus.Deserialize(timerInfo);

        List<RulesInfo> rulesInfos = handleRequest(scheduleStatus.getLast() == null ? TimestampExtension.now() : scheduleStatus.getLast(), context);
        return rulesInfos;
    }
}