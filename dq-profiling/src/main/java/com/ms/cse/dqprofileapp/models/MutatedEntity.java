package com.ms.cse.dqprofileapp.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class MutatedEntity {
    @Getter
    @Setter
    private String typeName;

    @Getter
    @Setter
    private MutatedEntityAttributes attributes;

    @Getter
    @Setter
    private UUID guid;
}
