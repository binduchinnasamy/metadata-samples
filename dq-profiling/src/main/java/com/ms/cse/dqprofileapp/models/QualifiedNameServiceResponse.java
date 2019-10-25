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

    @Override
    public String toString() {
        return "{ \"isExists\": \"" + this.isExists + "\", \"qualifiedName\": \"" + this.qualifiedName + "\", \"guid\": \"" + this.guid + "\", \"error_code\": \"" + error_code + "\", \"error_description\": \"" + this.error_description + "\" }";
    }

}
