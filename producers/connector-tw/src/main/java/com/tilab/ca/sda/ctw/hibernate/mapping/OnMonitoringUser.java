package com.tilab.ca.sda.ctw.hibernate.mapping;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "on_monitoring_users")
public class OnMonitoringUser implements Serializable {

    @Id
    @Column(name = "uid")
    private long uid;

    @Column(name = "on_monitoring_from")
    private Date onMonitoringFrom = null;

    @Column(name = "monitor_from_node")
    private String monitorFromNode = null;

    
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Date getOnMonitoringFrom() {
        return onMonitoringFrom;
    }

    public void setOnMonitoringFrom(Date onMonitoringFrom) {
        this.onMonitoringFrom = onMonitoringFrom;
    }

    public String getMonitorFromNode() {
        return monitorFromNode;
    }

    public void setMonitorFromNode(String monitorFromNode) {
        this.monitorFromNode = monitorFromNode;
    }
    
}
