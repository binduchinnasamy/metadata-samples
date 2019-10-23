package com.ms.cse.dqprofileapp.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DQ_RULESINFO")
public class RulesInfo {

    @Id
    @Getter @Setter private String ruleId;
    @Getter @Setter private String ruleName;
    @Getter @Setter private String dimension;
    @Getter @Setter private String columnName;

    @Column(name = "\"Rule Description\"")
    @Getter @Setter private String ruleDescription;

    @Column(name = "\"Business Rule Name\"")
    @Getter @Setter private String businessRuleName;

    @Column(name = "\"Update Timestamp\"")
    @Getter @Setter private Timestamp updateTimestamp;
    @Getter @Setter private String ruleType;

    @Column(name = "TABLE_FQDN")
    @Getter @Setter private String tableFQDN;

    @Column(name = "COLUMN_FQDN")
    @Getter @Setter private String columnFQDN;
    @Getter @Setter private String tableName;
    @Getter @Setter private String ruleDefinition;
    @Getter @Setter private String rulePriority;
}

