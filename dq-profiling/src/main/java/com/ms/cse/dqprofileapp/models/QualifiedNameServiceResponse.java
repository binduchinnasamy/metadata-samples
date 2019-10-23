package com.ms.cse.dqprofileapp.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class QualifiedNameServiceResponse {
    @Getter
    @Setter
    private boolean isExists;

    @Getter
    @Setter
    private String qualifiedName;

    @Getter
    @Setter
    private UUID guid;

    @Getter
    @Setter
    private String error_code;

    @Getter
    @Setter
    private String error_description;

}
