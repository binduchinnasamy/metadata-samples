package com.ms.cse.dqprofileapp;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.EntityScore;
import com.ms.cse.dqprofileapp.models.ScheduleStatus;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Timestamp;
import java.util.List;

//@ComponentScan(basePackages={"com.ms.cse.dqprofileapp"})
public class EntityScoreHandler extends AzureSpringBootRequestHandler<Timestamp, List<EntityScore>> {
    
    //@FunctionName("getCurrentEntityScores")
    public List<EntityScore> execute(
            @TimerTrigger(name = "getCurrentEntityScoresTrigger", schedule = "0 */2 * * * *") String timerInfo,
            ExecutionContext context) {

        ScheduleStatus scheduleStatus = ScheduleStatus.Deserialize(timerInfo);
        List<EntityScore> entityScores = handleRequest(scheduleStatus.getLast() == null ? TimestampExtension.now() : scheduleStatus.getLast(), context);
        return entityScores;
    }
}