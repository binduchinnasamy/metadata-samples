package com.ms.cse.dqprofileapp.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "VW_ENTITYSCORE")
public class EntityScore {
    @Id
    private int entityId;

    @Column(name = "\"ROWS PASSED\"")
    private int rowsPassed;

    @Column(name = "\"ROWS FAILED\"")
    private int rowsFailed;

    @Column(name = "\"TOTAL ROWS\"")
    private int totalRows;

    @Column(name = "\"Update Timestamp\"")
    private Timestamp updateTimestamp;

    public int getEntityId(){ return this.entityId; }
    public int getRowsPassed() { return this.rowsPassed; }
    public int getRowsFailed() { return this.rowsFailed; }
    public int getTotalRows() { return this.totalRows; }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setEntityId(int entityId){
        this.entityId = entityId;
    }

    public void setRowsPassed(int rowsPassed) {
        this.rowsPassed = rowsPassed;
    }

    public void setRowsFailed(int rowsFailed) {
        this.rowsFailed = rowsFailed;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}
