package com.mycompany.wh.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for PlanningWorkOrder from external API
 */
public class PlanningWorkOrderDTO {

    private String priority;
    private String id;
    private String state;
    private String profile;
    private String planningWorkOrderId;
    private LocalDateTime latestTimeCreateDnlnvl;
    private String parentWorkOrderId;
    private String numOfReturnMaterial;
    private String workOrderTypeName;
    private String planningWorkOrdercol;
    private String statusReturnMaterial;
    private String scadaQuantityOut1;
    private String scadaQuantityOut;
    private String scadaUserFullname;
    private String status;
    private String note;
    private LocalDateTime createTime;
    private String quantityActual;
    private String workOrderType;
    private Integer numberStaff;
    private String reasonId;
    private String scadaAssetId;
    private String productType;
    private String scadaUserGroup;
    private LocalDateTime lastUpdateTime;
    private String processStatus;
    private String scadaStageList;
    private String recurrenceRule;
    private String scadaUserName;
    private String bomVersion;
    private String classify;
    private LocalDateTime endTime;
    private String sapWoId;
    private String woId;
    private String lineId;
    private String isNew;
    private Integer quota;
    private String profileId;
    private String productId;
    private LocalDateTime startTime;
    private String profileName;
    private String productOrderId;
    private String lotNumber;
    private String productCode;
    private String branchCode;
    private String groupName;
    private String productName;
    private String branchName;
    private Integer quantityPlan;
    private String groupCode;
    private String phoneNumber;
    private String customerName;
    private String customerCode;

    // Getters and setters
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPlanningWorkOrderId() {
        return planningWorkOrderId;
    }

    public void setPlanningWorkOrderId(String planningWorkOrderId) {
        this.planningWorkOrderId = planningWorkOrderId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public LocalDateTime getLatestTimeCreateDnlnvl() {
        return latestTimeCreateDnlnvl;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public void setLatestTimeCreateDnlnvl(
        LocalDateTime latestTimeCreateDnlnvl
    ) {
        this.latestTimeCreateDnlnvl = latestTimeCreateDnlnvl;
    }

    public String getParentWorkOrderId() {
        return parentWorkOrderId;
    }

    public void setParentWorkOrderId(String parentWorkOrderId) {
        this.parentWorkOrderId = parentWorkOrderId;
    }

    public String getNumOfReturnMaterial() {
        return numOfReturnMaterial;
    }

    public void setNumOfReturnMaterial(String numOfReturnMaterial) {
        this.numOfReturnMaterial = numOfReturnMaterial;
    }

    public String getWorkOrderTypeName() {
        return workOrderTypeName;
    }

    public void setWorkOrderTypeName(String workOrderTypeName) {
        this.workOrderTypeName = workOrderTypeName;
    }

    public String getPlanningWorkOrdercol() {
        return planningWorkOrdercol;
    }

    public void setPlanningWorkOrdercol(String planningWorkOrdercol) {
        this.planningWorkOrdercol = planningWorkOrdercol;
    }

    public String getStatusReturnMaterial() {
        return statusReturnMaterial;
    }

    public void setStatusReturnMaterial(String statusReturnMaterial) {
        this.statusReturnMaterial = statusReturnMaterial;
    }

    public String getScadaQuantityOut1() {
        return scadaQuantityOut1;
    }

    public void setScadaQuantityOut1(String scadaQuantityOut1) {
        this.scadaQuantityOut1 = scadaQuantityOut1;
    }

    public String getScadaQuantityOut() {
        return scadaQuantityOut;
    }

    public void setScadaQuantityOut(String scadaQuantityOut) {
        this.scadaQuantityOut = scadaQuantityOut;
    }

    public String getScadaUserFullname() {
        return scadaUserFullname;
    }

    public void setScadaUserFullname(String scadaUserFullname) {
        this.scadaUserFullname = scadaUserFullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getQuantityActual() {
        return quantityActual;
    }

    public void setQuantityActual(String quantityActual) {
        this.quantityActual = quantityActual;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }

    public Integer getNumberStaff() {
        return numberStaff;
    }

    public void setNumberStaff(Integer numberStaff) {
        this.numberStaff = numberStaff;
    }

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public String getScadaAssetId() {
        return scadaAssetId;
    }

    public void setScadaAssetId(String scadaAssetId) {
        this.scadaAssetId = scadaAssetId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getScadaUserGroup() {
        return scadaUserGroup;
    }

    public void setScadaUserGroup(String scadaUserGroup) {
        this.scadaUserGroup = scadaUserGroup;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getScadaStageList() {
        return scadaStageList;
    }

    public void setScadaStageList(String scadaStageList) {
        this.scadaStageList = scadaStageList;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public String getScadaUserName() {
        return scadaUserName;
    }

    public void setScadaUserName(String scadaUserName) {
        this.scadaUserName = scadaUserName;
    }

    public String getBomVersion() {
        return bomVersion;
    }

    public void setBomVersion(String bomVersion) {
        this.bomVersion = bomVersion;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getSapWoId() {
        return sapWoId;
    }

    public void setSapWoId(String sapWoId) {
        this.sapWoId = sapWoId;
    }

    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(String productOrderId) {
        this.productOrderId = productOrderId;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getQuantityPlan() {
        return quantityPlan;
    }

    public void setQuantityPlan(Integer quantityPlan) {
        this.quantityPlan = quantityPlan;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
