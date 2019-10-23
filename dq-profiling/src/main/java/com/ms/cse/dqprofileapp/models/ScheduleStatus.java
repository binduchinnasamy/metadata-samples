package com.ms.cse.dqprofileapp.models;

import com.ms.cse.dqprofileapp.extensions.TimestampExtension;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.Timestamp;

public class ScheduleStatus {
    @Getter
    @Setter
    private Timestamp next;

    @Getter
    @Setter
    private Timestamp last;

    @Getter
    @Setter
    private Timestamp lastUpdated;

    public static ScheduleStatus Deserialize(String json) {
        if(json != null && !json.isEmpty()) {
            JSONObject jo = new JSONObject(json);
            JSONObject scheduleStatusRoot = (JSONObject) jo.get("ScheduleStatus");

            ScheduleStatus scheduleStatus = new ScheduleStatus();
            scheduleStatus.last = TimestampExtension.fromISO8601(scheduleStatusRoot.get("Last").toString());
            scheduleStatus.next = TimestampExtension.fromISO8601(scheduleStatusRoot.get("Next").toString());
            scheduleStatus.lastUpdated = TimestampExtension.fromISO8601(scheduleStatusRoot.get("LastUpdated").toString());
            return scheduleStatus;
        } else {
            return new ScheduleStatus();
        }
    }
}
