package com.ms.cse.dqprofileapp.models;

import lombok.Getter;
import lombok.Setter;

public class JsonWrapperEntity {
    @Getter
    @Setter
    private String entity_type_name;

    @Getter
    @Setter
    private JsonWrapperEntityAttribute[] attributes;

    @Getter
    @Setter
    private String created_by;
}
