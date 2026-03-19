package com.mycompany.myapp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A SapPoInfo.
 */
@Entity
@Table(name = "SAP_PO_Info")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SapPoInfo implements Serializable {

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
    @Column(name = "OPOR_Branch", length = 510)
    private String oporBranch;

    @Size(max = 510)
    @Column(name = "OPOR_CANCELED", length = 510)
    private String oporCanceled;

    @Size(max = 510)
    @Column(name = "OPOR_CardCode", length = 510)
    private String oporCardCode;

    @Size(max = 510)
    @Column(name = "OPOR_CardName", length = 510)
    private String oporCardName;

    @Size(max = 510)
    @Column(name = "OPOR_Comments", length = 510)
    private String oporComments;

    @Column(name = "OPOR_CreateDate")
    private Instant oporCreateDate;

    @Size(max = 510)
    @Column(name = "OPOR_Department", length = 510)
    private String oporDepartment;

    @Column(name = "OPOR_DocDate")
    private Instant oporDocDate;

    @Column(name = "OPOR_DocDueDate")
    private Instant oporDocDueDate;

    @Size(max = 510)
    @Column(name = "OPOR_DocEntry", length = 510)
    private String oporDocEntry;

    @Size(max = 510)
    @Column(name = "OPOR_DocNum", length = 510)
    private String oporDocNum;

    @Size(max = 510)
    @Column(name = "OPOR_DocStatus", length = 510)
    private String oporDocStatus;

    @Size(max = 510)
    @Column(name = "OPOR_InvntSttus", length = 510)
    private String oporInvntSttus;

    @Size(max = 510)
    @Column(name = "OPOR_JrnlMemo", length = 510)
    private String oporJrnlMemo;

    @Size(max = 510)
    @Column(name = "OPOR_U_CoAdd", length = 510)
    private String oporUCoAdd;

    @Size(max = 510)
    @Column(name = "OPOR_U_CodeInv", length = 510)
    private String oporUCodeInv;

    @Size(max = 510)
    @Column(name = "OPOR_U_CodeSerial", length = 510)
    private String oporUCodeSerial;

    @Size(max = 510)
    @Column(name = "OPOR_U_ContractDate", length = 510)
    private String oporUContractDate;

    @Size(max = 510)
    @Column(name = "OPOR_U_DeclarePd", length = 510)
    private String oporUDeclarePd;

    @Size(max = 510)
    @Column(name = "OPOR_U_DocNum", length = 510)
    private String oporUDocNum;

    @Size(max = 510)
    @Column(name = "OPOR_U_InvCode", length = 510)
    private String oporUInvCode;

    @Size(max = 510)
    @Column(name = "OPOR_U_InvCode2", length = 510)
    private String oporUInvCode2;

    @Size(max = 510)
    @Column(name = "OPOR_U_InvSerial", length = 510)
    private String oporUInvSerial;

    @Size(max = 510)
    @Column(name = "OPOR_U_Pur_NVGiao", length = 510)
    private String oporUPurNVGiao;

    @Column(name = "OPOR_UpdateDate")
    private Instant oporUpdateDate;

    @Size(max = 510)
    @Column(name = "OPOR_UserSign", length = 510)
    private String oporUserSign;

    @Column(name = "OPOR_TaxDate")
    private Instant oporTaxDate;

    @Column(name = "OPOR_CntctCode")
    private Integer oporCntctCode;

    @Size(max = 510)
    @Column(name = "OPOR_NumAtCard", length = 510)
    private String oporNumAtCard;

    @Column(name = "OPOR_SlpCode")
    private Integer oporSlpCode;

    @Column(name = "OPOR_OwnerCode")
    private Integer oporOwnerCode;

    @Column(name = "OPOR_VatSum", precision = 21, scale = 2)
    private BigDecimal oporVatSum;

    @Column(name = "OPOR_DocTotal", precision = 21, scale = 2)
    private BigDecimal oporDocTotal;

    @Column(name = "OPOR_VatSumSy", precision = 21, scale = 2)
    private BigDecimal oporVatSumSy;

    @Size(max = 510)
    @Column(name = "OPOR_U_ht", length = 510)
    private String oporUHt;

    @Size(max = 510)
    @Column(name = "OPOR_U_Payment", length = 510)
    private String oporUPayment;

    /**
     * Line Fields (POR1)
     */
    @Schema(description = "Line Fields (POR1)")
    @Size(max = 510)
    @Column(name = "POR1_BaseDocNum", length = 510)
    private String por1BaseDocNum;

    @Size(max = 510)
    @Column(name = "POR1_BaseEntry", length = 510)
    private String por1BaseEntry;

    @Size(max = 510)
    @Column(name = "POR1_BaseLine", length = 510)
    private String por1BaseLine;

    @Size(max = 510)
    @Column(name = "POR1_BaseRef", length = 510)
    private String por1BaseRef;

    @Size(max = 510)
    @Column(name = "POR1_Currency", length = 510)
    private String por1Currency;

    @Size(max = 510)
    @Column(name = "POR1_DiscPrcnt", length = 510)
    private String por1DiscPrcnt;

    @Size(max = 510)
    @Column(name = "PO_DocEntry", length = 510)
    private String poDocEntry;

    @Size(max = 510)
    @Column(name = "POR1_Dscription", length = 510)
    private String por1Dscription;

    @Size(max = 510)
    @Column(name = "POR1_ItemCode", length = 510)
    private String por1ItemCode;

    @Size(max = 510)
    @Column(name = "POR1_LineNum", length = 510)
    private String por1LineNum;

    @Size(max = 510)
    @Column(name = "POR1_LineStatus", length = 510)
    private String por1LineStatus;

    @Size(max = 510)
    @Column(name = "POR1_LineVendor", length = 510)
    private String por1LineVendor;

    @Size(max = 510)
    @Column(name = "POR1_OpenSumSys", length = 510)
    private String por1OpenSumSys;

    @Size(max = 510)
    @Column(name = "POR1_Price", length = 510)
    private String por1Price;

    @Size(max = 510)
    @Column(name = "POR1_Quantity", length = 510)
    private String por1Quantity;

    @Column(name = "POR1_ShipDate")
    private Instant por1ShipDate;

    @Size(max = 510)
    @Column(name = "POR1_TotalFrgn", length = 510)
    private String por1TotalFrgn;

    @Size(max = 510)
    @Column(name = "POR1_TotalSumsy", length = 510)
    private String por1TotalSumsy;

    @Size(max = 510)
    @Column(name = "POR1_TrgetEntry", length = 510)
    private String por1TrgetEntry;

    @Size(max = 510)
    @Column(name = "POR1_U_Mcode", length = 510)
    private String por1UMcode;

    @Size(max = 510)
    @Column(name = "POR1_U_SO", length = 510)
    private String por1USo;

    @Size(max = 510)
    @Column(name = "POR1_U_Tenkythuat", length = 510)
    private String por1UTenkythuat;

    @Size(max = 510)
    @Column(name = "POR1_UnitMsr", length = 510)
    private String por1UnitMsr;

    @Size(max = 510)
    @Column(name = "POR1_UOMCode", length = 510)
    private String por1UOMCode;

    @Size(max = 510)
    @Column(name = "POR1_VatGroup", length = 510)
    private String por1VatGroup;

    @Column(name = "POR1_LineTotal", precision = 21, scale = 2)
    private BigDecimal por1LineTotal;

    @Column(name = "POR1_VatPrcnt", precision = 21, scale = 2)
    private BigDecimal por1VatPrcnt;

    @Column(name = "POR1_PriceAfVat", precision = 21, scale = 2)
    private BigDecimal por1PriceAfVat;

    @Size(max = 510)
    @Column(name = "POR1_WhsCode", length = 510)
    private String por1WhsCode;

    /**
     * Other Fields
     */
    @Schema(description = "Other Fields")
    @Column(name = "PrMapPo")
    private String prMapPo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SapPoInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOporBranch() {
        return this.oporBranch;
    }

    public SapPoInfo oporBranch(String oporBranch) {
        this.setOporBranch(oporBranch);
        return this;
    }

    public void setOporBranch(String oporBranch) {
        this.oporBranch = oporBranch;
    }

    public String getOporCanceled() {
        return this.oporCanceled;
    }

    public SapPoInfo oporCanceled(String oporCanceled) {
        this.setOporCanceled(oporCanceled);
        return this;
    }

    public void setOporCanceled(String oporCanceled) {
        this.oporCanceled = oporCanceled;
    }

    public String getOporCardCode() {
        return this.oporCardCode;
    }

    public SapPoInfo oporCardCode(String oporCardCode) {
        this.setOporCardCode(oporCardCode);
        return this;
    }

    public void setOporCardCode(String oporCardCode) {
        this.oporCardCode = oporCardCode;
    }

    public String getOporCardName() {
        return this.oporCardName;
    }

    public SapPoInfo oporCardName(String oporCardName) {
        this.setOporCardName(oporCardName);
        return this;
    }

    public void setOporCardName(String oporCardName) {
        this.oporCardName = oporCardName;
    }

    public String getOporComments() {
        return this.oporComments;
    }

    public SapPoInfo oporComments(String oporComments) {
        this.setOporComments(oporComments);
        return this;
    }

    public void setOporComments(String oporComments) {
        this.oporComments = oporComments;
    }

    public Instant getOporCreateDate() {
        return this.oporCreateDate;
    }

    public SapPoInfo oporCreateDate(Instant oporCreateDate) {
        this.setOporCreateDate(oporCreateDate);
        return this;
    }

    public void setOporCreateDate(Instant oporCreateDate) {
        this.oporCreateDate = oporCreateDate;
    }

    public String getOporDepartment() {
        return this.oporDepartment;
    }

    public SapPoInfo oporDepartment(String oporDepartment) {
        this.setOporDepartment(oporDepartment);
        return this;
    }

    public void setOporDepartment(String oporDepartment) {
        this.oporDepartment = oporDepartment;
    }

    public Instant getOporDocDate() {
        return this.oporDocDate;
    }

    public SapPoInfo oporDocDate(Instant oporDocDate) {
        this.setOporDocDate(oporDocDate);
        return this;
    }

    public void setOporDocDate(Instant oporDocDate) {
        this.oporDocDate = oporDocDate;
    }

    public Instant getOporDocDueDate() {
        return this.oporDocDueDate;
    }

    public SapPoInfo oporDocDueDate(Instant oporDocDueDate) {
        this.setOporDocDueDate(oporDocDueDate);
        return this;
    }

    public void setOporDocDueDate(Instant oporDocDueDate) {
        this.oporDocDueDate = oporDocDueDate;
    }

    public String getOporDocEntry() {
        return this.oporDocEntry;
    }

    public SapPoInfo oporDocEntry(String oporDocEntry) {
        this.setOporDocEntry(oporDocEntry);
        return this;
    }

    public void setOporDocEntry(String oporDocEntry) {
        this.oporDocEntry = oporDocEntry;
    }

    public String getOporDocNum() {
        return this.oporDocNum;
    }

    public SapPoInfo oporDocNum(String oporDocNum) {
        this.setOporDocNum(oporDocNum);
        return this;
    }

    public void setOporDocNum(String oporDocNum) {
        this.oporDocNum = oporDocNum;
    }

    public String getOporDocStatus() {
        return this.oporDocStatus;
    }

    public SapPoInfo oporDocStatus(String oporDocStatus) {
        this.setOporDocStatus(oporDocStatus);
        return this;
    }

    public void setOporDocStatus(String oporDocStatus) {
        this.oporDocStatus = oporDocStatus;
    }

    public String getOporInvntSttus() {
        return this.oporInvntSttus;
    }

    public SapPoInfo oporInvntSttus(String oporInvntSttus) {
        this.setOporInvntSttus(oporInvntSttus);
        return this;
    }

    public void setOporInvntSttus(String oporInvntSttus) {
        this.oporInvntSttus = oporInvntSttus;
    }

    public String getOporJrnlMemo() {
        return this.oporJrnlMemo;
    }

    public SapPoInfo oporJrnlMemo(String oporJrnlMemo) {
        this.setOporJrnlMemo(oporJrnlMemo);
        return this;
    }

    public void setOporJrnlMemo(String oporJrnlMemo) {
        this.oporJrnlMemo = oporJrnlMemo;
    }

    public String getOporUCoAdd() {
        return this.oporUCoAdd;
    }

    public SapPoInfo oporUCoAdd(String oporUCoAdd) {
        this.setOporUCoAdd(oporUCoAdd);
        return this;
    }

    public void setOporUCoAdd(String oporUCoAdd) {
        this.oporUCoAdd = oporUCoAdd;
    }

    public String getOporUCodeInv() {
        return this.oporUCodeInv;
    }

    public SapPoInfo oporUCodeInv(String oporUCodeInv) {
        this.setOporUCodeInv(oporUCodeInv);
        return this;
    }

    public void setOporUCodeInv(String oporUCodeInv) {
        this.oporUCodeInv = oporUCodeInv;
    }

    public String getOporUCodeSerial() {
        return this.oporUCodeSerial;
    }

    public SapPoInfo oporUCodeSerial(String oporUCodeSerial) {
        this.setOporUCodeSerial(oporUCodeSerial);
        return this;
    }

    public void setOporUCodeSerial(String oporUCodeSerial) {
        this.oporUCodeSerial = oporUCodeSerial;
    }

    public String getOporUContractDate() {
        return this.oporUContractDate;
    }

    public SapPoInfo oporUContractDate(String oporUContractDate) {
        this.setOporUContractDate(oporUContractDate);
        return this;
    }

    public void setOporUContractDate(String oporUContractDate) {
        this.oporUContractDate = oporUContractDate;
    }

    public String getOporUDeclarePd() {
        return this.oporUDeclarePd;
    }

    public SapPoInfo oporUDeclarePd(String oporUDeclarePd) {
        this.setOporUDeclarePd(oporUDeclarePd);
        return this;
    }

    public void setOporUDeclarePd(String oporUDeclarePd) {
        this.oporUDeclarePd = oporUDeclarePd;
    }

    public String getOporUDocNum() {
        return this.oporUDocNum;
    }

    public SapPoInfo oporUDocNum(String oporUDocNum) {
        this.setOporUDocNum(oporUDocNum);
        return this;
    }

    public void setOporUDocNum(String oporUDocNum) {
        this.oporUDocNum = oporUDocNum;
    }

    public String getOporUInvCode() {
        return this.oporUInvCode;
    }

    public SapPoInfo oporUInvCode(String oporUInvCode) {
        this.setOporUInvCode(oporUInvCode);
        return this;
    }

    public void setOporUInvCode(String oporUInvCode) {
        this.oporUInvCode = oporUInvCode;
    }

    public String getOporUInvCode2() {
        return this.oporUInvCode2;
    }

    public SapPoInfo oporUInvCode2(String oporUInvCode2) {
        this.setOporUInvCode2(oporUInvCode2);
        return this;
    }

    public void setOporUInvCode2(String oporUInvCode2) {
        this.oporUInvCode2 = oporUInvCode2;
    }

    public String getOporUInvSerial() {
        return this.oporUInvSerial;
    }

    public SapPoInfo oporUInvSerial(String oporUInvSerial) {
        this.setOporUInvSerial(oporUInvSerial);
        return this;
    }

    public void setOporUInvSerial(String oporUInvSerial) {
        this.oporUInvSerial = oporUInvSerial;
    }

    public String getOporUPurNVGiao() {
        return this.oporUPurNVGiao;
    }

    public SapPoInfo oporUPurNVGiao(String oporUPurNVGiao) {
        this.setOporUPurNVGiao(oporUPurNVGiao);
        return this;
    }

    public void setOporUPurNVGiao(String oporUPurNVGiao) {
        this.oporUPurNVGiao = oporUPurNVGiao;
    }

    public Instant getOporUpdateDate() {
        return this.oporUpdateDate;
    }

    public SapPoInfo oporUpdateDate(Instant oporUpdateDate) {
        this.setOporUpdateDate(oporUpdateDate);
        return this;
    }

    public void setOporUpdateDate(Instant oporUpdateDate) {
        this.oporUpdateDate = oporUpdateDate;
    }

    public String getOporUserSign() {
        return this.oporUserSign;
    }

    public SapPoInfo oporUserSign(String oporUserSign) {
        this.setOporUserSign(oporUserSign);
        return this;
    }

    public void setOporUserSign(String oporUserSign) {
        this.oporUserSign = oporUserSign;
    }

    public Instant getOporTaxDate() {
        return this.oporTaxDate;
    }

    public SapPoInfo oporTaxDate(Instant oporTaxDate) {
        this.setOporTaxDate(oporTaxDate);
        return this;
    }

    public void setOporTaxDate(Instant oporTaxDate) {
        this.oporTaxDate = oporTaxDate;
    }

    public Integer getOporCntctCode() {
        return this.oporCntctCode;
    }

    public SapPoInfo oporCntctCode(Integer oporCntctCode) {
        this.setOporCntctCode(oporCntctCode);
        return this;
    }

    public void setOporCntctCode(Integer oporCntctCode) {
        this.oporCntctCode = oporCntctCode;
    }

    public String getOporNumAtCard() {
        return this.oporNumAtCard;
    }

    public SapPoInfo oporNumAtCard(String oporNumAtCard) {
        this.setOporNumAtCard(oporNumAtCard);
        return this;
    }

    public void setOporNumAtCard(String oporNumAtCard) {
        this.oporNumAtCard = oporNumAtCard;
    }

    public Integer getOporSlpCode() {
        return this.oporSlpCode;
    }

    public SapPoInfo oporSlpCode(Integer oporSlpCode) {
        this.setOporSlpCode(oporSlpCode);
        return this;
    }

    public void setOporSlpCode(Integer oporSlpCode) {
        this.oporSlpCode = oporSlpCode;
    }

    public Integer getOporOwnerCode() {
        return this.oporOwnerCode;
    }

    public SapPoInfo oporOwnerCode(Integer oporOwnerCode) {
        this.setOporOwnerCode(oporOwnerCode);
        return this;
    }

    public void setOporOwnerCode(Integer oporOwnerCode) {
        this.oporOwnerCode = oporOwnerCode;
    }

    public BigDecimal getOporVatSum() {
        return this.oporVatSum;
    }

    public SapPoInfo oporVatSum(BigDecimal oporVatSum) {
        this.setOporVatSum(oporVatSum);
        return this;
    }

    public void setOporVatSum(BigDecimal oporVatSum) {
        this.oporVatSum = oporVatSum;
    }

    public BigDecimal getOporDocTotal() {
        return this.oporDocTotal;
    }

    public SapPoInfo oporDocTotal(BigDecimal oporDocTotal) {
        this.setOporDocTotal(oporDocTotal);
        return this;
    }

    public void setOporDocTotal(BigDecimal oporDocTotal) {
        this.oporDocTotal = oporDocTotal;
    }

    public BigDecimal getOporVatSumSy() {
        return this.oporVatSumSy;
    }

    public SapPoInfo oporVatSumSy(BigDecimal oporVatSumSy) {
        this.setOporVatSumSy(oporVatSumSy);
        return this;
    }

    public void setOporVatSumSy(BigDecimal oporVatSumSy) {
        this.oporVatSumSy = oporVatSumSy;
    }

    public String getOporUHt() {
        return this.oporUHt;
    }

    public SapPoInfo oporUHt(String oporUHt) {
        this.setOporUHt(oporUHt);
        return this;
    }

    public void setOporUHt(String oporUHt) {
        this.oporUHt = oporUHt;
    }

    public String getOporUPayment() {
        return this.oporUPayment;
    }

    public SapPoInfo oporUPayment(String oporUPayment) {
        this.setOporUPayment(oporUPayment);
        return this;
    }

    public void setOporUPayment(String oporUPayment) {
        this.oporUPayment = oporUPayment;
    }

    public String getPor1BaseDocNum() {
        return this.por1BaseDocNum;
    }

    public SapPoInfo por1BaseDocNum(String por1BaseDocNum) {
        this.setPor1BaseDocNum(por1BaseDocNum);
        return this;
    }

    public void setPor1BaseDocNum(String por1BaseDocNum) {
        this.por1BaseDocNum = por1BaseDocNum;
    }

    public String getPor1BaseEntry() {
        return this.por1BaseEntry;
    }

    public SapPoInfo por1BaseEntry(String por1BaseEntry) {
        this.setPor1BaseEntry(por1BaseEntry);
        return this;
    }

    public void setPor1BaseEntry(String por1BaseEntry) {
        this.por1BaseEntry = por1BaseEntry;
    }

    public String getPor1BaseLine() {
        return this.por1BaseLine;
    }

    public SapPoInfo por1BaseLine(String por1BaseLine) {
        this.setPor1BaseLine(por1BaseLine);
        return this;
    }

    public void setPor1BaseLine(String por1BaseLine) {
        this.por1BaseLine = por1BaseLine;
    }

    public String getPor1BaseRef() {
        return this.por1BaseRef;
    }

    public SapPoInfo por1BaseRef(String por1BaseRef) {
        this.setPor1BaseRef(por1BaseRef);
        return this;
    }

    public void setPor1BaseRef(String por1BaseRef) {
        this.por1BaseRef = por1BaseRef;
    }

    public String getPor1Currency() {
        return this.por1Currency;
    }

    public SapPoInfo por1Currency(String por1Currency) {
        this.setPor1Currency(por1Currency);
        return this;
    }

    public void setPor1Currency(String por1Currency) {
        this.por1Currency = por1Currency;
    }

    public String getPor1DiscPrcnt() {
        return this.por1DiscPrcnt;
    }

    public SapPoInfo por1DiscPrcnt(String por1DiscPrcnt) {
        this.setPor1DiscPrcnt(por1DiscPrcnt);
        return this;
    }

    public void setPor1DiscPrcnt(String por1DiscPrcnt) {
        this.por1DiscPrcnt = por1DiscPrcnt;
    }

    public String getPoDocEntry() {
        return this.poDocEntry;
    }

    public SapPoInfo poDocEntry(String poDocEntry) {
        this.setPoDocEntry(poDocEntry);
        return this;
    }

    public void setPoDocEntry(String poDocEntry) {
        this.poDocEntry = poDocEntry;
    }

    public String getPor1Dscription() {
        return this.por1Dscription;
    }

    public SapPoInfo por1Dscription(String por1Dscription) {
        this.setPor1Dscription(por1Dscription);
        return this;
    }

    public void setPor1Dscription(String por1Dscription) {
        this.por1Dscription = por1Dscription;
    }

    public String getPor1ItemCode() {
        return this.por1ItemCode;
    }

    public SapPoInfo por1ItemCode(String por1ItemCode) {
        this.setPor1ItemCode(por1ItemCode);
        return this;
    }

    public void setPor1ItemCode(String por1ItemCode) {
        this.por1ItemCode = por1ItemCode;
    }

    public String getPor1LineNum() {
        return this.por1LineNum;
    }

    public SapPoInfo por1LineNum(String por1LineNum) {
        this.setPor1LineNum(por1LineNum);
        return this;
    }

    public void setPor1LineNum(String por1LineNum) {
        this.por1LineNum = por1LineNum;
    }

    public String getPor1LineStatus() {
        return this.por1LineStatus;
    }

    public SapPoInfo por1LineStatus(String por1LineStatus) {
        this.setPor1LineStatus(por1LineStatus);
        return this;
    }

    public void setPor1LineStatus(String por1LineStatus) {
        this.por1LineStatus = por1LineStatus;
    }

    public String getPor1LineVendor() {
        return this.por1LineVendor;
    }

    public SapPoInfo por1LineVendor(String por1LineVendor) {
        this.setPor1LineVendor(por1LineVendor);
        return this;
    }

    public void setPor1LineVendor(String por1LineVendor) {
        this.por1LineVendor = por1LineVendor;
    }

    public String getPor1OpenSumSys() {
        return this.por1OpenSumSys;
    }

    public SapPoInfo por1OpenSumSys(String por1OpenSumSys) {
        this.setPor1OpenSumSys(por1OpenSumSys);
        return this;
    }

    public void setPor1OpenSumSys(String por1OpenSumSys) {
        this.por1OpenSumSys = por1OpenSumSys;
    }

    public String getPor1Price() {
        return this.por1Price;
    }

    public SapPoInfo por1Price(String por1Price) {
        this.setPor1Price(por1Price);
        return this;
    }

    public void setPor1Price(String por1Price) {
        this.por1Price = por1Price;
    }

    public String getPor1Quantity() {
        return this.por1Quantity;
    }

    public SapPoInfo por1Quantity(String por1Quantity) {
        this.setPor1Quantity(por1Quantity);
        return this;
    }

    public void setPor1Quantity(String por1Quantity) {
        this.por1Quantity = por1Quantity;
    }

    public Instant getPor1ShipDate() {
        return this.por1ShipDate;
    }

    public SapPoInfo por1ShipDate(Instant por1ShipDate) {
        this.setPor1ShipDate(por1ShipDate);
        return this;
    }

    public void setPor1ShipDate(Instant por1ShipDate) {
        this.por1ShipDate = por1ShipDate;
    }

    public String getPor1TotalFrgn() {
        return this.por1TotalFrgn;
    }

    public SapPoInfo por1TotalFrgn(String por1TotalFrgn) {
        this.setPor1TotalFrgn(por1TotalFrgn);
        return this;
    }

    public void setPor1TotalFrgn(String por1TotalFrgn) {
        this.por1TotalFrgn = por1TotalFrgn;
    }

    public String getPor1TotalSumsy() {
        return this.por1TotalSumsy;
    }

    public SapPoInfo por1TotalSumsy(String por1TotalSumsy) {
        this.setPor1TotalSumsy(por1TotalSumsy);
        return this;
    }

    public void setPor1TotalSumsy(String por1TotalSumsy) {
        this.por1TotalSumsy = por1TotalSumsy;
    }

    public String getPor1TrgetEntry() {
        return this.por1TrgetEntry;
    }

    public SapPoInfo por1TrgetEntry(String por1TrgetEntry) {
        this.setPor1TrgetEntry(por1TrgetEntry);
        return this;
    }

    public void setPor1TrgetEntry(String por1TrgetEntry) {
        this.por1TrgetEntry = por1TrgetEntry;
    }

    public String getPor1UMcode() {
        return this.por1UMcode;
    }

    public SapPoInfo por1UMcode(String por1UMcode) {
        this.setPor1UMcode(por1UMcode);
        return this;
    }

    public void setPor1UMcode(String por1UMcode) {
        this.por1UMcode = por1UMcode;
    }

    public String getPor1USo() {
        return this.por1USo;
    }

    public SapPoInfo por1USo(String por1USo) {
        this.setPor1USo(por1USo);
        return this;
    }

    public void setPor1USo(String por1USo) {
        this.por1USo = por1USo;
    }

    public String getPor1UTenkythuat() {
        return this.por1UTenkythuat;
    }

    public SapPoInfo por1UTenkythuat(String por1UTenkythuat) {
        this.setPor1UTenkythuat(por1UTenkythuat);
        return this;
    }

    public void setPor1UTenkythuat(String por1UTenkythuat) {
        this.por1UTenkythuat = por1UTenkythuat;
    }

    public String getPor1UnitMsr() {
        return this.por1UnitMsr;
    }

    public SapPoInfo por1UnitMsr(String por1UnitMsr) {
        this.setPor1UnitMsr(por1UnitMsr);
        return this;
    }

    public void setPor1UnitMsr(String por1UnitMsr) {
        this.por1UnitMsr = por1UnitMsr;
    }

    public String getPor1UOMCode() {
        return this.por1UOMCode;
    }

    public SapPoInfo por1UOMCode(String por1UOMCode) {
        this.setPor1UOMCode(por1UOMCode);
        return this;
    }

    public void setPor1UOMCode(String por1UOMCode) {
        this.por1UOMCode = por1UOMCode;
    }

    public String getPor1VatGroup() {
        return this.por1VatGroup;
    }

    public SapPoInfo por1VatGroup(String por1VatGroup) {
        this.setPor1VatGroup(por1VatGroup);
        return this;
    }

    public void setPor1VatGroup(String por1VatGroup) {
        this.por1VatGroup = por1VatGroup;
    }

    public BigDecimal getPor1LineTotal() {
        return this.por1LineTotal;
    }

    public SapPoInfo por1LineTotal(BigDecimal por1LineTotal) {
        this.setPor1LineTotal(por1LineTotal);
        return this;
    }

    public void setPor1LineTotal(BigDecimal por1LineTotal) {
        this.por1LineTotal = por1LineTotal;
    }

    public BigDecimal getPor1VatPrcnt() {
        return this.por1VatPrcnt;
    }

    public SapPoInfo por1VatPrcnt(BigDecimal por1VatPrcnt) {
        this.setPor1VatPrcnt(por1VatPrcnt);
        return this;
    }

    public void setPor1VatPrcnt(BigDecimal por1VatPrcnt) {
        this.por1VatPrcnt = por1VatPrcnt;
    }

    public BigDecimal getPor1PriceAfVat() {
        return this.por1PriceAfVat;
    }

    public SapPoInfo por1PriceAfVat(BigDecimal por1PriceAfVat) {
        this.setPor1PriceAfVat(por1PriceAfVat);
        return this;
    }

    public void setPor1PriceAfVat(BigDecimal por1PriceAfVat) {
        this.por1PriceAfVat = por1PriceAfVat;
    }

    public String getPor1WhsCode() {
        return this.por1WhsCode;
    }

    public SapPoInfo por1WhsCode(String por1WhsCode) {
        this.setPor1WhsCode(por1WhsCode);
        return this;
    }

    public void setPor1WhsCode(String por1WhsCode) {
        this.por1WhsCode = por1WhsCode;
    }

    public String getPrMapPo() {
        return this.prMapPo;
    }

    public SapPoInfo prMapPo(String prMapPo) {
        this.setPrMapPo(prMapPo);
        return this;
    }

    public void setPrMapPo(String prMapPo) {
        this.prMapPo = prMapPo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SapPoInfo)) {
            return false;
        }
        return getId() != null && getId().equals(((SapPoInfo) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
  public String toString() {
    return "SapPoInfo{" +
        "id=" + getId() +
        ", oporBranch='" + getOporBranch() + "'" +
        ", oporCanceled='" + getOporCanceled() + "'" +
        ", oporCardCode='" + getOporCardCode() + "'" +
        ", oporCardName='" + getOporCardName() + "'" +
        ", oporComments='" + getOporComments() + "'" +
        ", oporCreateDate='" + getOporCreateDate() + "'" +
        ", oporDepartment='" + getOporDepartment() + "'" +
        ", oporDocDate='" + getOporDocDate() + "'" +
        ", oporDocDueDate='" + getOporDocDueDate() + "'" +
        ", oporDocEntry='" + getOporDocEntry() + "'" +
        ", oporDocNum='" + getOporDocNum() + "'" +
        ", oporDocStatus='" + getOporDocStatus() + "'" +
        ", oporInvntSttus='" + getOporInvntSttus() + "'" +
        ", oporJrnlMemo='" + getOporJrnlMemo() + "'" +
        ", oporUCoAdd='" + getOporUCoAdd() + "'" +
        ", oporUCodeInv='" + getOporUCodeInv() + "'" +
        ", oporUCodeSerial='" + getOporUCodeSerial() + "'" +
        ", oporUContractDate='" + getOporUContractDate() + "'" +
        ", oporUDeclarePd='" + getOporUDeclarePd() + "'" +
        ", oporUDocNum='" + getOporUDocNum() + "'" +
        ", oporUInvCode='" + getOporUInvCode() + "'" +
        ", oporUInvCode2='" + getOporUInvCode2() + "'" +
        ", oporUInvSerial='" + getOporUInvSerial() + "'" +
        ", oporUPurNVGiao='" + getOporUPurNVGiao() + "'" +
        ", oporUpdateDate='" + getOporUpdateDate() + "'" +
        ", oporUserSign='" + getOporUserSign() + "'" +
        ", oporTaxDate='" + getOporTaxDate() + "'" +
        ", oporCntctCode=" + getOporCntctCode() +
        ", oporNumAtCard='" + getOporNumAtCard() + "'" +
        ", oporSlpCode=" + getOporSlpCode() +
        ", oporOwnerCode=" + getOporOwnerCode() +
        ", oporVatSum=" + getOporVatSum() +
        ", oporDocTotal=" + getOporDocTotal() +
        ", oporVatSumSy=" + getOporVatSumSy() +
        ", oporUHt='" + getOporUHt() + "'" +
        ", oporUPayment='" + getOporUPayment() + "'" +
        ", por1BaseDocNum='" + getPor1BaseDocNum() + "'" +
        ", por1BaseEntry='" + getPor1BaseEntry() + "'" +
        ", por1BaseLine='" + getPor1BaseLine() + "'" +
        ", por1BaseRef='" + getPor1BaseRef() + "'" +
        ", por1Currency='" + getPor1Currency() + "'" +
        ", por1DiscPrcnt='" + getPor1DiscPrcnt() + "'" +
        ", poDocEntry='" + getPoDocEntry() + "'" +
        ", por1Dscription='" + getPor1Dscription() + "'" +
        ", por1ItemCode='" + getPor1ItemCode() + "'" +
        ", por1LineNum='" + getPor1LineNum() + "'" +
        ", por1LineStatus='" + getPor1LineStatus() + "'" +
        ", por1LineVendor='" + getPor1LineVendor() + "'" +
        ", por1OpenSumSys='" + getPor1OpenSumSys() + "'" +
        ", por1Price='" + getPor1Price() + "'" +
        ", por1Quantity='" + getPor1Quantity() + "'" +
        ", por1ShipDate='" + getPor1ShipDate() + "'" +
        ", por1TotalFrgn='" + getPor1TotalFrgn() + "'" +
        ", por1TotalSumsy='" + getPor1TotalSumsy() + "'" +
        ", por1TrgetEntry='" + getPor1TrgetEntry() + "'" +
        ", por1UMcode='" + getPor1UMcode() + "'" +
        ", por1USo='" + getPor1USo() + "'" +
        ", por1UTenkythuat='" + getPor1UTenkythuat() + "'" +
        ", por1UnitMsr='" + getPor1UnitMsr() + "'" +
        ", por1UOMCode='" + getPor1UOMCode() + "'" +
        ", por1VatGroup='" + getPor1VatGroup() + "'" +
        ", por1LineTotal=" + getPor1LineTotal() +
        ", por1VatPrcnt=" + getPor1VatPrcnt() +
        ", por1PriceAfVat=" + getPor1PriceAfVat() +
        ", por1WhsCode='" + getPor1WhsCode() + "'" +
        ", prMapPo='" + getPrMapPo() + "'" +
        "}";
  }
}
