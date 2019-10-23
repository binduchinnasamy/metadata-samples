package com.ms.cse.dqprofileapp;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import com.ms.cse.dqprofileapp.models.ColumnScore;
import com.ms.cse.dqprofileapp.models.ScheduleStatus;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Timestamp;
import java.util.List;

//@ComponentScan(basePackages={"com.ms.cse.dqprofileapp"})
public class ColumnScoreHandler extends AzureSpringBootRequestHandler<Timestamp, List<ColumnScore>> {

    //@FunctionName("getCurrentColumnScores")
    public List<ColumnScore> execute(
            @TimerTrigger(name = "getCurrentColumnScoresTrigger", schedule = "0 */2 * * * *") String timerInfo,
            ExecutionContext context) {

        ScheduleStatus scheduleStatus = ScheduleStatus.Deserialize(timerInfo);
        List<ColumnScore> columnScores = handleRequest(scheduleStatus.getLast() == null ? TimestampExtension.now() : scheduleStatus.getLast(), context);
        return columnScores;
    }
}