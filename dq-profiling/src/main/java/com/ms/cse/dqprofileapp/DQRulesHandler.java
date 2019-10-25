package com.ms.cse.dqprofileapp;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.FunctionInput;
import com.ms.cse.dqprofileapp.models.RulesInfo;
import com.ms.cse.dqprofileapp.models.ScheduleStatus;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@ComponentScan(basePackages={"com.ms.cse.dqprofileapp"})
public class DQRulesHandler extends AzureSpringBootRequestHandler<FunctionInput, Integer> {

    @FunctionName("getLatestDQRules")
    public Integer execute(
            @TimerTrigger(name = "getLatestDQRulesTrigger", schedule = "0 */2 * * * *") String timerInfo,
            ExecutionContext context) {

        ScheduleStatus scheduleStatus = ScheduleStatus.Deserialize(timerInfo);
        FunctionInput input = new FunctionInput();
        input.setTimeStamp(scheduleStatus.getLast() == null ? TimestampExtension.now() : scheduleStatus.getLast());
        input.setExecutionContext(context);
        return handleRequest(input, context);
    }
}