package com.ms.cse.dqprofileapp.extensions;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TimestampExtension {

    public static Timestamp minTimestamp() {
        String minDateAsISO8601 = "1970-01-01T00:00:00.000000Z";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        TemporalAccessor accessor = timeFormatter.parse(minDateAsISO8601);
        Timestamp minTimestamp = fromInstant(Instant.from(accessor));
        return minTimestamp;
    }

    public static Timestamp fromInstant(Instant instant) {
        return Timestamp.valueOf(instant.toString().replace('T', ' ').replace("Z", ""));
    }

    public static Timestamp fromISO8601(String dateAsISO8601) {
        if(dateAsISO8601.startsWith("0001-01-01")) {
            return minTimestamp();
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        TemporalAccessor accessor = timeFormatter.parse(dateAsISO8601);
        Timestamp timestamp = fromInstant(Instant.from(accessor));
        return timestamp;
    }

    public static Timestamp now() {
        return fromInstant(Instant.now());
    }
}