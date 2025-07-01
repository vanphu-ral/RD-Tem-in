package com.mycompany.myapp.domain;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "scan_work_order")
public class scanWorkorder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "work_order")
    private String workOrder;

    @Column(name = "lot")
    private String lot;

    @Column(name = "number_of_plan")
    private String numberOfPlan;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "working")
    private Integer working;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "run_time")
    private Integer runTime;

    public scanWorkorder() {}

    public scanWorkorder(
        Long orderId,
        String workOrder,
        String lot,
        String numberOfPlan,
        Long productId,
        Integer groupId,
        Integer working,
        Date createAt
    ) {
        this.orderId = orderId;
        this.workOrder = workOrder;
        this.lot = lot;
        this.numberOfPlan = numberOfPlan;
        this.productId = productId;
        this.groupId = groupId;
        this.working = working;
        this.createAt = createAt;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getNumberOfPlan() {
        return numberOfPlan;
    }

    public void setNumberOfPlan(String numberOfPlan) {
        this.numberOfPlan = numberOfPlan;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getWorking() {
        return working;
    }

    public void setWorking(Integer working) {
        this.working = working;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
