package com.ms.cse.dqprofileapp.models;

import kong.unirest.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    public static JsonWrapperEntity create(RulesInfo ruleInfo) {
        JsonWrapperEntityAttribute[] attributes = new JsonWrapperEntityAttribute[3];

        // rule_id
        JsonWrapperEntityAttribute ruleIdAttr = new JsonWrapperEntityAttribute();
        ruleIdAttr.setAttr_name("rule_id");
        ruleIdAttr.setAttr_value(ruleInfo.getRuleId());
        attributes[0] = ruleIdAttr;

        // name
        JsonWrapperEntityAttribute nameAttr = new JsonWrapperEntityAttribute();
        nameAttr.setAttr_name("name");
        nameAttr.setAttr_value(ruleInfo.getRuleId());
        attributes[1] = nameAttr;

        // qualifiedName
        JsonWrapperEntityAttribute qualifiedNameAttr = new JsonWrapperEntityAttribute();
        qualifiedNameAttr.setAttr_name("qualifiedName");
        qualifiedNameAttr.setAttr_value(ruleInfo.getColumnFQDN());
        attributes[2] = qualifiedNameAttr;

        JsonWrapperEntity entity = new JsonWrapperEntity();
        entity.setEntity_type_name("dq_rule");
        entity.setAttributes(attributes);
        entity.setCreated_by("dqrule_profiler");

        return entity;
    }

    public static JsonWrapperEntity[] from(List<JsonWrapperEntity> entities) {
        if (entities != null) {
            JsonWrapperEntity[] arr = new JsonWrapperEntity[entities.size()];
            arr = entities.toArray(arr);
            return arr;
        }
        return new JsonWrapperEntity[0];
    }
}
