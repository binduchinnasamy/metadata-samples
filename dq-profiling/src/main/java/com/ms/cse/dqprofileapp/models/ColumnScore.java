package com.ms.cse.dqprofileapp.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name ="VW_COLUMNSCORE")
public class ColumnScore {

    @Id
    @Column(name = "\"COLUMN FDQN\"")
    @Getter
    @Setter
    private String columnFqdn;

    @Column(name = "\"ROWS PASSED\"")
    @Getter
    @Setter
    private int rowsPassed;

    @Column(name = "\"ROWS FAILED\"")
    @Getter
    @Setter
    private int rowsFailed;

    @Column(name = "\"TOTAL ROWS\"")
    @Getter
    @Setter
    private int totalRows;

    @Column(name = "\"Update Timestamp\"")
    @Getter
    @Setter
    private Timestamp updateTimestamp;
}
