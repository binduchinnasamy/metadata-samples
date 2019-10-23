package com.ms.cse.dqprofileapp.models;

import com.microsoft.azure.functions.ExecutionContext;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


public class FunctionInput {

    @Getter
    @Setter
    private ExecutionContext executionContext;

    @Getter
    @Setter
    private Timestamp timeStamp;
}
