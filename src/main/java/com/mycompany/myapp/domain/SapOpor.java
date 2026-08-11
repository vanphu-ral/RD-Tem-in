package com.mycompany.myapp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "sap_opor")
public class SapOpor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequenceGenerator"
    )
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Header Fields (OPOR)
     */
    @Schema(description = "Header Fields (OPOR)")
    @Size(max = 510)
    @Column(name = "Branch", length = 510)
    private String oporBranch;

    @Size(max = 510)
    @Column(name = "CANCELED", length = 510)
    private String oporCanceled;

    @Size(max = 510)
    @Column(name = "CardCode", length = 510)
    private String oporCardCode;

    @Size(max = 510)
    @Column(name = "CardName", length = 510)
    private String oporCardName;

    @Size(max = 510)
    @Column(name = "Comments", length = 510)
    private String oporComments;

    @Column(name = "CreateDate")
    private Instant oporCreateDate;

    @Size(max = 510)
    @Column(name = "Department", length = 510)
    private String oporDepartment;

    @Column(name = "DocDate")
    private Instant oporDocDate;

    @Column(name = "DocDueDate")
    private Instant oporDocDueDate;

    @Size(max = 510)
    @Column(name = "DocEntry", length = 510)
    private String oporDocEntry;

    @Size(max = 510)
    @Column(name = "DocNum", length = 510)
    private String oporDocNum;

    @Size(max = 510)
    @Column(name = "DocStatus", length = 510)
    private String oporDocStatus;

    @Size(max = 510)
    @Column(name = "InvntSttus", length = 510)
    private String oporInvntSttus;

    @Size(max = 510)
    @Column(name = "JrnlMemo", length = 510)
    private String oporJrnlMemo;

    @Size(max = 510)
    @Column(name = "U_CoAdd", length = 510)
    private String oporUCoAdd;

    @Size(max = 510)
    @Column(name = "U_CodeInv", length = 510)
    private String oporUCodeInv;

    @Size(max = 510)
    @Column(name = "U_CodeSerial", length = 510)
    private String oporUCodeSerial;

    @Size(max = 510)
    @Column(name = "U_ContractDate", length = 510)
    private String oporUContractDate;

    @Size(max = 510)
    @Column(name = "U_DeclarePd", length = 510)
    private String oporUDeclarePd;

    @Size(max = 510)
    @Column(name = "U_DocNum", length = 510)
    private String oporUDocNum;

    @Size(max = 510)
    @Column(name = "U_InvCode", length = 510)
    private String oporUInvCode;

    @Size(max = 510)
    @Column(name = "U_InvCode2", length = 510)
    private String oporUInvCode2;

    @Size(max = 510)
    @Column(name = "U_InvSerial", length = 510)
    private String oporUInvSerial;

    @Size(max = 510)
    @Column(name = "U_Pur_NvGiao", length = 510)
    private String oporUPurNVGiao;

    @Column(name = "UpdateDate")
    private Instant oporUpdateDate;

    @Size(max = 510)
    @Column(name = "UserSign", length = 510)
    private String oporUserSign;

    @Column(name = "TaxDate")
    private Instant oporTaxDate;

    @Column(name = "CntctCode")
    private Integer oporCntctCode;

    @Size(max = 510)
    @Column(name = "NumAtCard", length = 510)
    private String oporNumAtCard;

    @Column(name = "SlpCode")
    private Integer oporSlpCode;

    @Column(name = "OwnerCode")
    private Integer oporOwnerCode;

    @Column(name = "VatSum", precision = 18, scale = 5)
    private BigDecimal oporVatSum;

    @Column(name = "DocTotal", precision = 18, scale = 5)
    private BigDecimal oporDocTotal;

    @Column(name = "VatSumSy", precision = 18, scale = 5)
    private BigDecimal oporVatSumSy;

    @Size(max = 510)
    @Column(name = "U_ht", length = 510)
    private String oporUHt;

    @Size(max = 510)
    @Column(name = "U_Payment", length = 510)
    private String oporUPayment;

    // --- GETTERS & SETTERS ---

    public Long getId() {
        return this.id;
    }

    public SapOpor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOporBranch() {
        return this.oporBranch;
    }

    public SapOpor oporBranch(String oporBranch) {
        this.setOporBranch(oporBranch);
        return this;
    }

    public void setOporBranch(String oporBranch) {
        this.oporBranch = oporBranch;
    }

    public String getOporCanceled() {
        return this.oporCanceled;
    }

    public SapOpor oporCanceled(String oporCanceled) {
        this.setOporCanceled(oporCanceled);
        return this;
    }

    public void setOporCanceled(String oporCanceled) {
        this.oporCanceled = oporCanceled;
    }

    public String getOporCardCode() {
        return this.oporCardCode;
    }

    public SapOpor oporCardCode(String oporCardCode) {
        this.setOporCardCode(oporCardCode);
        return this;
    }

    public void setOporCardCode(String oporCardCode) {
        this.oporCardCode = oporCardCode;
    }

    public String getOporCardName() {
        return this.oporCardName;
    }

    public SapOpor oporCardName(String oporCardName) {
        this.setOporCardName(oporCardName);
        return this;
    }

    public void setOporCardName(String oporCardName) {
        this.oporCardName = oporCardName;
    }

    public String getOporComments() {
        return this.oporComments;
    }

    public SapOpor oporComments(String oporComments) {
        this.setOporComments(oporComments);
        return this;
    }

    public void setOporComments(String oporComments) {
        this.oporComments = oporComments;
    }

    public Instant getOporCreateDate() {
        return this.oporCreateDate;
    }

    public SapOpor oporCreateDate(Instant oporCreateDate) {
        this.setOporCreateDate(oporCreateDate);
        return this;
    }

    public void setOporCreateDate(Instant oporCreateDate) {
        this.oporCreateDate = oporCreateDate;
    }

    public String getOporDepartment() {
        return this.oporDepartment;
    }

    public SapOpor oporDepartment(String oporDepartment) {
        this.setOporDepartment(oporDepartment);
        return this;
    }

    public void setOporDepartment(String oporDepartment) {
        this.oporDepartment = oporDepartment;
    }

    public Instant getOporDocDate() {
        return this.oporDocDate;
    }

    public SapOpor oporDocDate(Instant oporDocDate) {
        this.setOporDocDate(oporDocDate);
        return this;
    }

    public void setOporDocDate(Instant oporDocDate) {
        this.oporDocDate = oporDocDate;
    }

    public Instant getOporDocDueDate() {
        return this.oporDocDueDate;
    }

    public SapOpor oporDocDueDate(Instant oporDocDueDate) {
        this.setOporDocDueDate(oporDocDueDate);
        return this;
    }

    public void setOporDocDueDate(Instant oporDocDueDate) {
        this.oporDocDueDate = oporDocDueDate;
    }

    public String getOporDocEntry() {
        return this.oporDocEntry;
    }

    public SapOpor oporDocEntry(String oporDocEntry) {
        this.setOporDocEntry(oporDocEntry);
        return this;
    }

    public void setOporDocEntry(String oporDocEntry) {
        this.oporDocEntry = oporDocEntry;
    }

    public String getOporDocNum() {
        return this.oporDocNum;
    }

    public SapOpor oporDocNum(String oporDocNum) {
        this.setOporDocNum(oporDocNum);
        return this;
    }

    public void setOporDocNum(String oporDocNum) {
        this.oporDocNum = oporDocNum;
    }

    public String getOporDocStatus() {
        return this.oporDocStatus;
    }

    public SapOpor oporDocStatus(String oporDocStatus) {
        this.setOporDocStatus(oporDocStatus);
        return this;
    }

    public void setOporDocStatus(String oporDocStatus) {
        this.oporDocStatus = oporDocStatus;
    }

    public String getOporInvntSttus() {
        return this.oporInvntSttus;
    }

    public SapOpor oporInvntSttus(String oporInvntSttus) {
        this.setOporInvntSttus(oporInvntSttus);
        return this;
    }

    public void setOporInvntSttus(String oporInvntSttus) {
        this.oporInvntSttus = oporInvntSttus;
    }

    public String getOporJrnlMemo() {
        return this.oporJrnlMemo;
    }

    public SapOpor oporJrnlMemo(String oporJrnlMemo) {
        this.setOporJrnlMemo(oporJrnlMemo);
        return this;
    }

    public void setOporJrnlMemo(String oporJrnlMemo) {
        this.oporJrnlMemo = oporJrnlMemo;
    }

    public String getOporUCoAdd() {
        return this.oporUCoAdd;
    }

    public SapOpor oporUCoAdd(String oporUCoAdd) {
        this.setOporUCoAdd(oporUCoAdd);
        return this;
    }

    public void setOporUCoAdd(String oporUCoAdd) {
        this.oporUCoAdd = oporUCoAdd;
    }

    public String getOporUCodeInv() {
        return this.oporUCodeInv;
    }

    public SapOpor oporUCodeInv(String oporUCodeInv) {
        this.setOporUCodeInv(oporUCodeInv);
        return this;
    }

    public void setOporUCodeInv(String oporUCodeInv) {
        this.oporUCodeInv = oporUCodeInv;
    }

    public String getOporUCodeSerial() {
        return this.oporUCodeSerial;
    }

    public SapOpor oporUCodeSerial(String oporUCodeSerial) {
        this.setOporUCodeSerial(oporUCodeSerial);
        return this;
    }

    public void setOporUCodeSerial(String oporUCodeSerial) {
        this.oporUCodeSerial = oporUCodeSerial;
    }

    public String getOporUContractDate() {
        return this.oporUContractDate;
    }

    public SapOpor oporUContractDate(String oporUContractDate) {
        this.setOporUContractDate(oporUContractDate);
        return this;
    }

    public void setOporUContractDate(String oporUContractDate) {
        this.oporUContractDate = oporUContractDate;
    }

    public String getOporUDeclarePd() {
        return this.oporUDeclarePd;
    }

    public SapOpor oporUDeclarePd(String oporUDeclarePd) {
        this.setOporUDeclarePd(oporUDeclarePd);
        return this;
    }

    public void setOporUDeclarePd(String oporUDeclarePd) {
        this.oporUDeclarePd = oporUDeclarePd;
    }

    public String getOporUDocNum() {
        return this.oporUDocNum;
    }

    public SapOpor oporUDocNum(String oporUDocNum) {
        this.setOporUDocNum(oporUDocNum);
        return this;
    }

    public void setOporUDocNum(String oporUDocNum) {
        this.oporUDocNum = oporUDocNum;
    }

    public String getOporUInvCode() {
        return this.oporUInvCode;
    }

    public SapOpor oporUInvCode(String oporUInvCode) {
        this.setOporUInvCode(oporUInvCode);
        return this;
    }

    public void setOporUInvCode(String oporUInvCode) {
        this.oporUInvCode = oporUInvCode;
    }

    public String getOporUInvCode2() {
        return this.oporUInvCode2;
    }

    public SapOpor oporUInvCode2(String oporUInvCode2) {
        this.setOporUInvCode2(oporUInvCode2);
        return this;
    }

    public void setOporUInvCode2(String oporUInvCode2) {
        this.oporUInvCode2 = oporUInvCode2;
    }

    public String getOporUInvSerial() {
        return this.oporUInvSerial;
    }

    public SapOpor oporUInvSerial(String oporUInvSerial) {
        this.setOporUInvSerial(oporUInvSerial);
        return this;
    }

    public void setOporUInvSerial(String oporUInvSerial) {
        this.oporUInvSerial = oporUInvSerial;
    }

    public String getOporUPurNVGiao() {
        return this.oporUPurNVGiao;
    }

    public SapOpor oporUPurNVGiao(String oporUPurNVGiao) {
        this.setOporUPurNVGiao(oporUPurNVGiao);
        return this;
    }

    public void setOporUPurNVGiao(String oporUPurNVGiao) {
        this.oporUPurNVGiao = oporUPurNVGiao;
    }

    public Instant getOporUpdateDate() {
        return this.oporUpdateDate;
    }

    public SapOpor oporUpdateDate(Instant oporUpdateDate) {
        this.setOporUpdateDate(oporUpdateDate);
        return this;
    }

    public void setOporUpdateDate(Instant oporUpdateDate) {
        this.oporUpdateDate = oporUpdateDate;
    }

    public String getOporUserSign() {
        return this.oporUserSign;
    }

    public SapOpor oporUserSign(String oporUserSign) {
        this.setOporUserSign(oporUserSign);
        return this;
    }

    public void setOporUserSign(String oporUserSign) {
        this.oporUserSign = oporUserSign;
    }

    public Instant getOporTaxDate() {
        return this.oporTaxDate;
    }

    public SapOpor oporTaxDate(Instant oporTaxDate) {
        this.setOporTaxDate(oporTaxDate);
        return this;
    }

    public void setOporTaxDate(Instant oporTaxDate) {
        this.oporTaxDate = oporTaxDate;
    }

    public Integer getOporCntctCode() {
        return this.oporCntctCode;
    }

    public SapOpor oporCntctCode(Integer oporCntctCode) {
        this.setOporCntctCode(oporCntctCode);
        return this;
    }

    public void setOporCntctCode(Integer oporCntctCode) {
        this.oporCntctCode = oporCntctCode;
    }

    public String getOporNumAtCard() {
        return this.oporNumAtCard;
    }

    public SapOpor oporNumAtCard(String oporNumAtCard) {
        this.setOporNumAtCard(oporNumAtCard);
        return this;
    }

    public void setOporNumAtCard(String oporNumAtCard) {
        this.oporNumAtCard = oporNumAtCard;
    }

    public Integer getOporSlpCode() {
        return this.oporSlpCode;
    }

    public SapOpor oporSlpCode(Integer oporSlpCode) {
        this.setOporSlpCode(oporSlpCode);
        return this;
    }

    public void setOporSlpCode(Integer oporSlpCode) {
        this.oporSlpCode = oporSlpCode;
    }

    public Integer getOporOwnerCode() {
        return this.oporOwnerCode;
    }

    public SapOpor oporOwnerCode(Integer oporOwnerCode) {
        this.setOporOwnerCode(oporOwnerCode);
        return this;
    }

    public void setOporOwnerCode(Integer oporOwnerCode) {
        this.oporOwnerCode = oporOwnerCode;
    }

    public BigDecimal getOporVatSum() {
        return this.oporVatSum;
    }

    public SapOpor oporVatSum(BigDecimal oporVatSum) {
        this.setOporVatSum(oporVatSum);
        return this;
    }

    public void setOporVatSum(BigDecimal oporVatSum) {
        this.oporVatSum = oporVatSum;
    }

    public BigDecimal getOporDocTotal() {
        return this.oporDocTotal;
    }

    public SapOpor oporDocTotal(BigDecimal oporDocTotal) {
        this.setOporDocTotal(oporDocTotal);
        return this;
    }

    public void setOporDocTotal(BigDecimal oporDocTotal) {
        this.oporDocTotal = oporDocTotal;
    }

    public BigDecimal getOporVatSumSy() {
        return this.oporVatSumSy;
    }

    public SapOpor oporVatSumSy(BigDecimal oporVatSumSy) {
        this.setOporVatSumSy(oporVatSumSy);
        return this;
    }

    public void setOporVatSumSy(BigDecimal oporVatSumSy) {
        this.oporVatSumSy = oporVatSumSy;
    }

    public String getOporUHt() {
        return this.oporUHt;
    }

    public SapOpor oporUHt(String oporUHt) {
        this.setOporUHt(oporUHt);
        return this;
    }

    public void setOporUHt(String oporUHt) {
        this.oporUHt = oporUHt;
    }

    public String getOporUPayment() {
        return this.oporUPayment;
    }

    public SapOpor oporUPayment(String oporUPayment) {
        this.setOporUPayment(oporUPayment);
        return this;
    }

    public void setOporUPayment(String oporUPayment) {
        this.oporUPayment = oporUPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapOpor)) {
            return false;
        }
        return getId() != null && getId().equals(((SapOpor) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "SapOpor{" +
            "id=" +
            getId() +
            ", oporBranch='" +
            getOporBranch() +
            "'" +
            ", oporCanceled='" +
            getOporCanceled() +
            "'" +
            ", oporCardCode='" +
            getOporCardCode() +
            "'" +
            ", oporCardName='" +
            getOporCardName() +
            "'" +
            ", oporComments='" +
            getOporComments() +
            "'" +
            ", oporCreateDate='" +
            getOporCreateDate() +
            "'" +
            ", oporDepartment='" +
            getOporDepartment() +
            "'" +
            ", oporDocDate='" +
            getOporDocDate() +
            "'" +
            ", oporDocDueDate='" +
            getOporDocDueDate() +
            "'" +
            ", oporDocEntry='" +
            getOporDocEntry() +
            "'" +
            ", oporDocNum='" +
            getOporDocNum() +
            "'" +
            ", oporDocStatus='" +
            getOporDocStatus() +
            "'" +
            ", oporInvntSttus='" +
            getOporInvntSttus() +
            "'" +
            ", oporJrnlMemo='" +
            getOporJrnlMemo() +
            "'" +
            ", oporUCoAdd='" +
            getOporUCoAdd() +
            "'" +
            ", oporUCodeInv='" +
            getOporUCodeInv() +
            "'" +
            ", oporUCodeSerial='" +
            getOporUCodeSerial() +
            "'" +
            ", oporUContractDate='" +
            getOporUContractDate() +
            "'" +
            ", oporUDeclarePd='" +
            getOporUDeclarePd() +
            "'" +
            ", oporUDocNum='" +
            getOporUDocNum() +
            "'" +
            ", oporUInvCode='" +
            getOporUInvCode() +
            "'" +
            ", oporUInvCode2='" +
            getOporUInvCode2() +
            "'" +
            ", oporUInvSerial='" +
            getOporUInvSerial() +
            "'" +
            ", oporUPurNVGiao='" +
            getOporUPurNVGiao() +
            "'" +
            ", oporUpdateDate='" +
            getOporUpdateDate() +
            "'" +
            ", oporUserSign='" +
            getOporUserSign() +
            "'" +
            ", oporTaxDate='" +
            getOporTaxDate() +
            "'" +
            ", oporCntctCode=" +
            getOporCntctCode() +
            ", oporNumAtCard='" +
            getOporNumAtCard() +
            "'" +
            ", oporSlpCode=" +
            getOporSlpCode() +
            ", oporOwnerCode=" +
            getOporOwnerCode() +
            ", oporVatSum=" +
            getOporVatSum() +
            ", oporDocTotal=" +
            getOporDocTotal() +
            ", oporVatSumSy=" +
            getOporVatSumSy() +
            ", oporUHt='" +
            getOporUHt() +
            "'" +
            ", oporUPayment='" +
            getOporUPayment() +
            "'" +
            "}"
        );
    }
}
