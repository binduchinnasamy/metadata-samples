package com.ms.cse.dqprofileapp.models;

import lombok.Getter;
import lombok.Setter;

public class Rule {
    @Getter @Setter private String guid;
    @Getter @Setter private String typeName;
    @Getter @Setter private RuleAttribute uniqueAttributes;

    //             "guid": "ed818998-8773-4502-bc0e-ca071a0e8265",
    //            "typeName": "dq_rule",
    //            "uniqueAttributes": {
    //                  "rule_id": "rule1",
    //                  "qualifiedName": "https://beamdatav2.dfs.core.windows.net/sales/2019/8/20/0/0/salesid/rule1"
    //             }
}
