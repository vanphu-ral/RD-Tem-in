package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

@Entity
@Table(name = "scan_detail_check")
public class scanDetailCheck implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "record_value")
    private String recordValue;

    @Column(name = "result")
    private String result;

    @Column(name = "position")
    private Integer position;

    @Column(name = "username")
    private String username;

    @Column(name = "create_at")
    private ZonedDateTime createAt;

    @Column(name = "record_name")
    private String recordName;

    @Column(name = "machine_id")
    private Long machineId;

    @Column(name = "time_recorded")
    private String timeRecorded;

    public String getTimeRecorded() {
        return timeRecorded;
    }

    public void setTimeRecorded(String timeRecorded) {
        this.timeRecorded = timeRecorded;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public scanDetailCheck() {}

    public scanDetailCheck(
        Long recordId,
        Long orderId,
        String recordValue,
        String result,
        Integer position,
        String username,
        ZonedDateTime createAt
    ) {
        this.recordId = recordId;
        this.orderId = orderId;
        this.recordValue = recordValue;
        this.result = result;
        this.position = position;
        this.username = username;
        this.createAt = createAt;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRecordValue() {
        return recordValue;
    }

    public void setRecordValue(String recordValue) {
        this.recordValue = recordValue;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ZonedDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(ZonedDateTime createAt) {
        this.createAt = createAt;
    }
}
