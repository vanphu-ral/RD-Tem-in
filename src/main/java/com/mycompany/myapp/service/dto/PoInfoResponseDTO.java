package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for PO Info response with details.
 * Contains main PO information (poInfo) and list of product details (poDetails).
 */
public class PoInfoResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO Info - Main header information
     */
    private PoInfoDTO poInfo;

    /**
     * PO Details - List of product details in the PO
     */
    private List<PoDetailDTO> poDetails;

    public PoInfoResponseDTO() {}

    public PoInfoResponseDTO(PoInfoDTO poInfo, List<PoDetailDTO> poDetails) {
        this.poInfo = poInfo;
        this.poDetails = poDetails;
    }

    public PoInfoDTO getPoInfo() {
        return poInfo;
    }

    public void setPoInfo(PoInfoDTO poInfo) {
        this.poInfo = poInfo;
    }

    public List<PoDetailDTO> getPoDetails() {
        return poDetails;
    }

    public void setPoDetails(List<PoDetailDTO> poDetails) {
        this.poDetails = poDetails;
    }

    /**
     * Inner class for PO Info (Header level - OPOR fields)
     */
    public static class PoInfoDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private String oporBranch;
        private String oporCanceled;
        private String oporCardCode;
        private String oporCardName;
        private String oporComments;
        private Instant oporCreateDate;
        private String oporDepartment;
        private Instant oporDocDate;
        private Instant oporDocDueDate;
        private String oporDocEntry;
        private String oporDocNum;
        private String oporDocStatus;
        private String oporInvntSttus;
        private String oporJrnlMemo;
        private String oporUCoAdd;
        private String oporUCodeInv;
        private String oporUCodeSerial;
        private String oporUContractDate;
        private String oporUDeclarePd;
        private String oporUDocNum;
        private String oporUInvCode;
        private String oporUInvCode2;
        private String oporUInvSerial;
        private String oporUPurNVGiao;
        private Instant oporUpdateDate;
        private String oporUserSign;
        private Instant oporTaxDate;
        private Integer oporCntctCode;
        private String oporNumAtCard;
        private Integer oporSlpCode;
        private Integer oporOwnerCode;
        private BigDecimal oporVatSum;
        private BigDecimal oporDocTotal;
        private BigDecimal oporVatSumSy;
        private String oporUHt;
        private String oporUPayment;
        private String prMapPo;

        public PoInfoDTO() {}

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOporBranch() {
            return oporBranch;
        }

        public void setOporBranch(String oporBranch) {
            this.oporBranch = oporBranch;
        }

        public String getOporCanceled() {
            return oporCanceled;
        }

        public void setOporCanceled(String oporCanceled) {
            this.oporCanceled = oporCanceled;
        }

        public String getOporCardCode() {
            return oporCardCode;
        }

        public void setOporCardCode(String oporCardCode) {
            this.oporCardCode = oporCardCode;
        }

        public String getOporCardName() {
            return oporCardName;
        }

        public void setOporCardName(String oporCardName) {
            this.oporCardName = oporCardName;
        }

        public String getOporComments() {
            return oporComments;
        }

        public void setOporComments(String oporComments) {
            this.oporComments = oporComments;
        }

        public Instant getOporCreateDate() {
            return oporCreateDate;
        }

        public void setOporCreateDate(Instant oporCreateDate) {
            this.oporCreateDate = oporCreateDate;
        }

        public String getOporDepartment() {
            return oporDepartment;
        }

        public void setOporDepartment(String oporDepartment) {
            this.oporDepartment = oporDepartment;
        }

        public Instant getOporDocDate() {
            return oporDocDate;
        }

        public void setOporDocDate(Instant oporDocDate) {
            this.oporDocDate = oporDocDate;
        }

        public Instant getOporDocDueDate() {
            return oporDocDueDate;
        }

        public void setOporDocDueDate(Instant oporDocDueDate) {
            this.oporDocDueDate = oporDocDueDate;
        }

        public String getOporDocEntry() {
            return oporDocEntry;
        }

        public void setOporDocEntry(String oporDocEntry) {
            this.oporDocEntry = oporDocEntry;
        }

        public String getOporDocNum() {
            return oporDocNum;
        }

        public void setOporDocNum(String oporDocNum) {
            this.oporDocNum = oporDocNum;
        }

        public String getOporDocStatus() {
            return oporDocStatus;
        }

        public void setOporDocStatus(String oporDocStatus) {
            this.oporDocStatus = oporDocStatus;
        }

        public String getOporInvntSttus() {
            return oporInvntSttus;
        }

        public void setOporInvntSttus(String oporInvntSttus) {
            this.oporInvntSttus = oporInvntSttus;
        }

        public String getOporJrnlMemo() {
            return oporJrnlMemo;
        }

        public void setOporJrnlMemo(String oporJrnlMemo) {
            this.oporJrnlMemo = oporJrnlMemo;
        }

        public String getOporUCoAdd() {
            return oporUCoAdd;
        }

        public void setOporUCoAdd(String oporUCoAdd) {
            this.oporUCoAdd = oporUCoAdd;
        }

        public String getOporUCodeInv() {
            return oporUCodeInv;
        }

        public void setOporUCodeInv(String oporUCodeInv) {
            this.oporUCodeInv = oporUCodeInv;
        }

        public String getOporUCodeSerial() {
            return oporUCodeSerial;
        }

        public void setOporUCodeSerial(String oporUCodeSerial) {
            this.oporUCodeSerial = oporUCodeSerial;
        }

        public String getOporUContractDate() {
            return oporUContractDate;
        }

        public void setOporUContractDate(String oporUContractDate) {
            this.oporUContractDate = oporUContractDate;
        }

        public String getOporUDeclarePd() {
            return oporUDeclarePd;
        }

        public void setOporUDeclarePd(String oporUDeclarePd) {
            this.oporUDeclarePd = oporUDeclarePd;
        }

        public String getOporUDocNum() {
            return oporUDocNum;
        }

        public void setOporUDocNum(String oporUDocNum) {
            this.oporUDocNum = oporUDocNum;
        }

        public String getOporUInvCode() {
            return oporUInvCode;
        }

        public void setOporUInvCode(String oporUInvCode) {
            this.oporUInvCode = oporUInvCode;
        }

        public String getOporUInvCode2() {
            return oporUInvCode2;
        }

        public void setOporUInvCode2(String oporUInvCode2) {
            this.oporUInvCode2 = oporUInvCode2;
        }

        public String getOporUInvSerial() {
            return oporUInvSerial;
        }

        public void setOporUInvSerial(String oporUInvSerial) {
            this.oporUInvSerial = oporUInvSerial;
        }

        public String getOporUPurNVGiao() {
            return oporUPurNVGiao;
        }

        public void setOporUPurNVGiao(String oporUPurNVGiao) {
            this.oporUPurNVGiao = oporUPurNVGiao;
        }

        public Instant getOporUpdateDate() {
            return oporUpdateDate;
        }

        public void setOporUpdateDate(Instant oporUpdateDate) {
            this.oporUpdateDate = oporUpdateDate;
        }

        public String getOporUserSign() {
            return oporUserSign;
        }

        public void setOporUserSign(String oporUserSign) {
            this.oporUserSign = oporUserSign;
        }

        public Instant getOporTaxDate() {
            return oporTaxDate;
        }

        public void setOporTaxDate(Instant oporTaxDate) {
            this.oporTaxDate = oporTaxDate;
        }

        public Integer getOporCntctCode() {
            return oporCntctCode;
        }

        public void setOporCntctCode(Integer oporCntctCode) {
            this.oporCntctCode = oporCntctCode;
        }

        public String getOporNumAtCard() {
            return oporNumAtCard;
        }

        public void setOporNumAtCard(String oporNumAtCard) {
            this.oporNumAtCard = oporNumAtCard;
        }

        public Integer getOporSlpCode() {
            return oporSlpCode;
        }

        public void setOporSlpCode(Integer oporSlpCode) {
            this.oporSlpCode = oporSlpCode;
        }

        public Integer getOporOwnerCode() {
            return oporOwnerCode;
        }

        public void setOporOwnerCode(Integer oporOwnerCode) {
            this.oporOwnerCode = oporOwnerCode;
        }

        public BigDecimal getOporVatSum() {
            return oporVatSum;
        }

        public void setOporVatSum(BigDecimal oporVatSum) {
            this.oporVatSum = oporVatSum;
        }

        public BigDecimal getOporDocTotal() {
            return oporDocTotal;
        }

        public void setOporDocTotal(BigDecimal oporDocTotal) {
            this.oporDocTotal = oporDocTotal;
        }

        public BigDecimal getOporVatSumSy() {
            return oporVatSumSy;
        }

        public void setOporVatSumSy(BigDecimal oporVatSumSy) {
            this.oporVatSumSy = oporVatSumSy;
        }

        public String getOporUHt() {
            return oporUHt;
        }

        public void setOporUHt(String oporUHt) {
            this.oporUHt = oporUHt;
        }

        public String getOporUPayment() {
            return oporUPayment;
        }

        public void setOporUPayment(String oporUPayment) {
            this.oporUPayment = oporUPayment;
        }

        public String getPrMapPo() {
            return prMapPo;
        }

        public void setPrMapPo(String prMapPo) {
            this.prMapPo = prMapPo;
        }
    }

    /**
     * Inner class for PO Detail (Line level - POR1 fields)
     */
    public static class PoDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private String por1BaseDocNum;
        private String por1BaseEntry;
        private String por1BaseLine;
        private String por1BaseRef;
        private String por1Currency;
        private String por1DiscPrcnt;
        private String poDocEntry;
        private String por1Dscription;
        private String por1ItemCode;
        private String por1LineNum;
        private String por1LineStatus;
        private String por1LineVendor;
        private String por1OpenSumSys;
        private String por1Price;
        private String por1Quantity;
        private Instant por1ShipDate;
        private String por1TotalFrgn;
        private String por1TotalSumsy;
        private String por1TrgetEntry;
        private String por1UMcode;
        private String por1USo;
        private String por1UTenkythuat;
        private String por1UnitMsr;
        private String por1UOMCode;
        private String por1VatGroup;
        private BigDecimal por1LineTotal;
        private BigDecimal por1VatPrcnt;
        private BigDecimal por1PriceAfVat;
        private String por1WhsCode;

        public PoDetailDTO() {}

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getPor1BaseDocNum() {
            return por1BaseDocNum;
        }

        public void setPor1BaseDocNum(String por1BaseDocNum) {
            this.por1BaseDocNum = por1BaseDocNum;
        }

        public String getPor1BaseEntry() {
            return por1BaseEntry;
        }

        public void setPor1BaseEntry(String por1BaseEntry) {
            this.por1BaseEntry = por1BaseEntry;
        }

        public String getPor1BaseLine() {
            return por1BaseLine;
        }

        public void setPor1BaseLine(String por1BaseLine) {
            this.por1BaseLine = por1BaseLine;
        }

        public String getPor1BaseRef() {
            return por1BaseRef;
        }

        public void setPor1BaseRef(String por1BaseRef) {
            this.por1BaseRef = por1BaseRef;
        }

        public String getPor1Currency() {
            return por1Currency;
        }

        public void setPor1Currency(String por1Currency) {
            this.por1Currency = por1Currency;
        }

        public String getPor1DiscPrcnt() {
            return por1DiscPrcnt;
        }

        public void setPor1DiscPrcnt(String por1DiscPrcnt) {
            this.por1DiscPrcnt = por1DiscPrcnt;
        }

        public String getPoDocEntry() {
            return poDocEntry;
        }

        public void setPoDocEntry(String poDocEntry) {
            this.poDocEntry = poDocEntry;
        }

        public String getPor1Dscription() {
            return por1Dscription;
        }

        public void setPor1Dscription(String por1Dscription) {
            this.por1Dscription = por1Dscription;
        }

        public String getPor1ItemCode() {
            return por1ItemCode;
        }

        public void setPor1ItemCode(String por1ItemCode) {
            this.por1ItemCode = por1ItemCode;
        }

        public String getPor1LineNum() {
            return por1LineNum;
        }

        public void setPor1LineNum(String por1LineNum) {
            this.por1LineNum = por1LineNum;
        }

        public String getPor1LineStatus() {
            return por1LineStatus;
        }

        public void setPor1LineStatus(String por1LineStatus) {
            this.por1LineStatus = por1LineStatus;
        }

        public String getPor1LineVendor() {
            return por1LineVendor;
        }

        public void setPor1LineVendor(String por1LineVendor) {
            this.por1LineVendor = por1LineVendor;
        }

        public String getPor1OpenSumSys() {
            return por1OpenSumSys;
        }

        public void setPor1OpenSumSys(String por1OpenSumSys) {
            this.por1OpenSumSys = por1OpenSumSys;
        }

        public String getPor1Price() {
            return por1Price;
        }

        public void setPor1Price(String por1Price) {
            this.por1Price = por1Price;
        }

        public String getPor1Quantity() {
            return por1Quantity;
        }

        public void setPor1Quantity(String por1Quantity) {
            this.por1Quantity = por1Quantity;
        }

        public Instant getPor1ShipDate() {
            return por1ShipDate;
        }

        public void setPor1ShipDate(Instant por1ShipDate) {
            this.por1ShipDate = por1ShipDate;
        }

        public String getPor1TotalFrgn() {
            return por1TotalFrgn;
        }

        public void setPor1TotalFrgn(String por1TotalFrgn) {
            this.por1TotalFrgn = por1TotalFrgn;
        }

        public String getPor1TotalSumsy() {
            return por1TotalSumsy;
        }

        public void setPor1TotalSumsy(String por1TotalSumsy) {
            this.por1TotalSumsy = por1TotalSumsy;
        }

        public String getPor1TrgetEntry() {
            return por1TrgetEntry;
        }

        public void setPor1TrgetEntry(String por1TrgetEntry) {
            this.por1TrgetEntry = por1TrgetEntry;
        }

        public String getPor1UMcode() {
            return por1UMcode;
        }

        public void setPor1UMcode(String por1UMcode) {
            this.por1UMcode = por1UMcode;
        }

        public String getPor1USo() {
            return por1USo;
        }

        public void setPor1USo(String por1USo) {
            this.por1USo = por1USo;
        }

        public String getPor1UTenkythuat() {
            return por1UTenkythuat;
        }

        public void setPor1UTenkythuat(String por1UTenkythuat) {
            this.por1UTenkythuat = por1UTenkythuat;
        }

        public String getPor1UnitMsr() {
            return por1UnitMsr;
        }

        public void setPor1UnitMsr(String por1UnitMsr) {
            this.por1UnitMsr = por1UnitMsr;
        }

        public String getPor1UOMCode() {
            return por1UOMCode;
        }

        public void setPor1UOMCode(String por1UOMCode) {
            this.por1UOMCode = por1UOMCode;
        }

        public String getPor1VatGroup() {
            return por1VatGroup;
        }

        public void setPor1VatGroup(String por1VatGroup) {
            this.por1VatGroup = por1VatGroup;
        }

        public BigDecimal getPor1LineTotal() {
            return por1LineTotal;
        }

        public void setPor1LineTotal(BigDecimal por1LineTotal) {
            this.por1LineTotal = por1LineTotal;
        }

        public BigDecimal getPor1VatPrcnt() {
            return por1VatPrcnt;
        }

        public void setPor1VatPrcnt(BigDecimal por1VatPrcnt) {
            this.por1VatPrcnt = por1VatPrcnt;
        }

        public BigDecimal getPor1PriceAfVat() {
            return por1PriceAfVat;
        }

        public void setPor1PriceAfVat(BigDecimal por1PriceAfVat) {
            this.por1PriceAfVat = por1PriceAfVat;
        }

        public String getPor1WhsCode() {
            return por1WhsCode;
        }

        public void setPor1WhsCode(String por1WhsCode) {
            this.por1WhsCode = por1WhsCode;
        }
    }
}
