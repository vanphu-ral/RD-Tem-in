package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
// import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.SapPoInfo} entity.
 * This class is used
 * in {@link com.mycompany.myapp.web.rest.SapPoInfoResource} to receive all the
 * possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sap-po-infos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
// @ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SapPoInfoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter oporBranch;

    private StringFilter oporCanceled;

    private StringFilter oporCardCode;

    private StringFilter oporCardName;

    private StringFilter oporComments;

    private InstantFilter oporCreateDate;

    private StringFilter oporDepartment;

    private InstantFilter oporDocDate;

    private InstantFilter oporDocDueDate;

    private StringFilter oporDocEntry;

    private StringFilter oporDocNum;

    private StringFilter oporDocStatus;

    private StringFilter oporInvntSttus;

    private StringFilter oporJrnlMemo;

    private StringFilter oporUCoAdd;

    private StringFilter oporUCodeInv;

    private StringFilter oporUCodeSerial;

    private StringFilter oporUContractDate;

    private StringFilter oporUDeclarePd;

    private StringFilter oporUDocNum;

    private StringFilter oporUInvCode;

    private StringFilter oporUInvCode2;

    private StringFilter oporUInvSerial;

    private StringFilter oporUPurNVGiao;

    private InstantFilter oporUpdateDate;

    private StringFilter oporUserSign;

    private InstantFilter oporTaxDate;

    private IntegerFilter oporCntctCode;

    private StringFilter oporNumAtCard;

    private IntegerFilter oporSlpCode;

    private IntegerFilter oporOwnerCode;

    private BigDecimalFilter oporVatSum;

    private BigDecimalFilter oporDocTotal;

    private BigDecimalFilter oporVatSumSy;

    private StringFilter oporUHt;

    private StringFilter oporUPayment;

    private StringFilter por1BaseDocNum;

    private StringFilter por1BaseEntry;

    private StringFilter por1BaseLine;

    private StringFilter por1BaseRef;

    private StringFilter por1Currency;

    private StringFilter por1DiscPrcnt;

    private StringFilter poDocEntry;

    private StringFilter por1Dscription;

    private StringFilter por1ItemCode;

    private StringFilter por1LineNum;

    private StringFilter por1LineStatus;

    private StringFilter por1LineVendor;

    private StringFilter por1OpenSumSys;

    private StringFilter por1Price;

    private StringFilter por1Quantity;

    private InstantFilter por1ShipDate;

    private StringFilter por1TotalFrgn;

    private StringFilter por1TotalSumsy;

    private StringFilter por1TrgetEntry;

    private StringFilter por1UMcode;

    private StringFilter por1USo;

    private StringFilter por1UTenkythuat;

    private StringFilter por1UnitMsr;

    private StringFilter por1UOMCode;

    private StringFilter por1VatGroup;

    private BigDecimalFilter por1LineTotal;

    private BigDecimalFilter por1VatPrcnt;

    private BigDecimalFilter por1PriceAfVat;

    private StringFilter por1WhsCode;

    private StringFilter prMapPo;

    private Boolean distinct;

    public SapPoInfoCriteria() {}

    public SapPoInfoCriteria(SapPoInfoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.oporBranch = other
            .optionalOporBranch()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporCanceled = other
            .optionalOporCanceled()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporCardCode = other
            .optionalOporCardCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporCardName = other
            .optionalOporCardName()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporComments = other
            .optionalOporComments()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporCreateDate = other
            .optionalOporCreateDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.oporDepartment = other
            .optionalOporDepartment()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporDocDate = other
            .optionalOporDocDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.oporDocDueDate = other
            .optionalOporDocDueDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.oporDocEntry = other
            .optionalOporDocEntry()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporDocNum = other
            .optionalOporDocNum()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporDocStatus = other
            .optionalOporDocStatus()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporInvntSttus = other
            .optionalOporInvntSttus()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporJrnlMemo = other
            .optionalOporJrnlMemo()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUCoAdd = other
            .optionalOporUCoAdd()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUCodeInv = other
            .optionalOporUCodeInv()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUCodeSerial = other
            .optionalOporUCodeSerial()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUContractDate = other
            .optionalOporUContractDate()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUDeclarePd = other
            .optionalOporUDeclarePd()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUDocNum = other
            .optionalOporUDocNum()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUInvCode = other
            .optionalOporUInvCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUInvCode2 = other
            .optionalOporUInvCode2()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUInvSerial = other
            .optionalOporUInvSerial()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUPurNVGiao = other
            .optionalOporUPurNVGiao()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUpdateDate = other
            .optionalOporUpdateDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.oporUserSign = other
            .optionalOporUserSign()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporTaxDate = other
            .optionalOporTaxDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.oporCntctCode = other
            .optionalOporCntctCode()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.oporNumAtCard = other
            .optionalOporNumAtCard()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporSlpCode = other
            .optionalOporSlpCode()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.oporOwnerCode = other
            .optionalOporOwnerCode()
            .map(IntegerFilter::copy)
            .orElse(null);
        this.oporVatSum = other
            .optionalOporVatSum()
            .map(BigDecimalFilter::copy)
            .orElse(null);
        this.oporDocTotal = other
            .optionalOporDocTotal()
            .map(BigDecimalFilter::copy)
            .orElse(null);
        this.oporVatSumSy = other
            .optionalOporVatSumSy()
            .map(BigDecimalFilter::copy)
            .orElse(null);
        this.oporUHt = other
            .optionalOporUHt()
            .map(StringFilter::copy)
            .orElse(null);
        this.oporUPayment = other
            .optionalOporUPayment()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1BaseDocNum = other
            .optionalPor1BaseDocNum()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1BaseEntry = other
            .optionalPor1BaseEntry()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1BaseLine = other
            .optionalPor1BaseLine()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1BaseRef = other
            .optionalPor1BaseRef()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1Currency = other
            .optionalPor1Currency()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1DiscPrcnt = other
            .optionalPor1DiscPrcnt()
            .map(StringFilter::copy)
            .orElse(null);
        this.poDocEntry = other
            .optionalPoDocEntry()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1Dscription = other
            .optionalPor1Dscription()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1ItemCode = other
            .optionalPor1ItemCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1LineNum = other
            .optionalPor1LineNum()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1LineStatus = other
            .optionalPor1LineStatus()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1LineVendor = other
            .optionalPor1LineVendor()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1OpenSumSys = other
            .optionalPor1OpenSumSys()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1Price = other
            .optionalPor1Price()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1Quantity = other
            .optionalPor1Quantity()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1ShipDate = other
            .optionalPor1ShipDate()
            .map(InstantFilter::copy)
            .orElse(null);
        this.por1TotalFrgn = other
            .optionalPor1TotalFrgn()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1TotalSumsy = other
            .optionalPor1TotalSumsy()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1TrgetEntry = other
            .optionalPor1TrgetEntry()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1UMcode = other
            .optionalPor1UMcode()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1USo = other
            .optionalPor1USo()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1UTenkythuat = other
            .optionalPor1UTenkythuat()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1UnitMsr = other
            .optionalPor1UnitMsr()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1UOMCode = other
            .optionalPor1UOMCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1VatGroup = other
            .optionalPor1VatGroup()
            .map(StringFilter::copy)
            .orElse(null);
        this.por1LineTotal = other
            .optionalPor1LineTotal()
            .map(BigDecimalFilter::copy)
            .orElse(null);
        this.por1VatPrcnt = other
            .optionalPor1VatPrcnt()
            .map(BigDecimalFilter::copy)
            .orElse(null);
        this.por1PriceAfVat = other
            .optionalPor1PriceAfVat()
            .map(BigDecimalFilter::copy)
            .orElse(null);
        this.por1WhsCode = other
            .optionalPor1WhsCode()
            .map(StringFilter::copy)
            .orElse(null);
        this.prMapPo = other
            .optionalPrMapPo()
            .map(StringFilter::copy)
            .orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SapPoInfoCriteria copy() {
        return new SapPoInfoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getOporBranch() {
        return oporBranch;
    }

    public Optional<StringFilter> optionalOporBranch() {
        return Optional.ofNullable(oporBranch);
    }

    public StringFilter oporBranch() {
        if (oporBranch == null) {
            setOporBranch(new StringFilter());
        }
        return oporBranch;
    }

    public void setOporBranch(StringFilter oporBranch) {
        this.oporBranch = oporBranch;
    }

    public StringFilter getOporCanceled() {
        return oporCanceled;
    }

    public Optional<StringFilter> optionalOporCanceled() {
        return Optional.ofNullable(oporCanceled);
    }

    public StringFilter oporCanceled() {
        if (oporCanceled == null) {
            setOporCanceled(new StringFilter());
        }
        return oporCanceled;
    }

    public void setOporCanceled(StringFilter oporCanceled) {
        this.oporCanceled = oporCanceled;
    }

    public StringFilter getOporCardCode() {
        return oporCardCode;
    }

    public Optional<StringFilter> optionalOporCardCode() {
        return Optional.ofNullable(oporCardCode);
    }

    public StringFilter oporCardCode() {
        if (oporCardCode == null) {
            setOporCardCode(new StringFilter());
        }
        return oporCardCode;
    }

    public void setOporCardCode(StringFilter oporCardCode) {
        this.oporCardCode = oporCardCode;
    }

    public StringFilter getOporCardName() {
        return oporCardName;
    }

    public Optional<StringFilter> optionalOporCardName() {
        return Optional.ofNullable(oporCardName);
    }

    public StringFilter oporCardName() {
        if (oporCardName == null) {
            setOporCardName(new StringFilter());
        }
        return oporCardName;
    }

    public void setOporCardName(StringFilter oporCardName) {
        this.oporCardName = oporCardName;
    }

    public StringFilter getOporComments() {
        return oporComments;
    }

    public Optional<StringFilter> optionalOporComments() {
        return Optional.ofNullable(oporComments);
    }

    public StringFilter oporComments() {
        if (oporComments == null) {
            setOporComments(new StringFilter());
        }
        return oporComments;
    }

    public void setOporComments(StringFilter oporComments) {
        this.oporComments = oporComments;
    }

    public InstantFilter getOporCreateDate() {
        return oporCreateDate;
    }

    public Optional<InstantFilter> optionalOporCreateDate() {
        return Optional.ofNullable(oporCreateDate);
    }

    public InstantFilter oporCreateDate() {
        if (oporCreateDate == null) {
            setOporCreateDate(new InstantFilter());
        }
        return oporCreateDate;
    }

    public void setOporCreateDate(InstantFilter oporCreateDate) {
        this.oporCreateDate = oporCreateDate;
    }

    public StringFilter getOporDepartment() {
        return oporDepartment;
    }

    public Optional<StringFilter> optionalOporDepartment() {
        return Optional.ofNullable(oporDepartment);
    }

    public StringFilter oporDepartment() {
        if (oporDepartment == null) {
            setOporDepartment(new StringFilter());
        }
        return oporDepartment;
    }

    public void setOporDepartment(StringFilter oporDepartment) {
        this.oporDepartment = oporDepartment;
    }

    public InstantFilter getOporDocDate() {
        return oporDocDate;
    }

    public Optional<InstantFilter> optionalOporDocDate() {
        return Optional.ofNullable(oporDocDate);
    }

    public InstantFilter oporDocDate() {
        if (oporDocDate == null) {
            setOporDocDate(new InstantFilter());
        }
        return oporDocDate;
    }

    public void setOporDocDate(InstantFilter oporDocDate) {
        this.oporDocDate = oporDocDate;
    }

    public InstantFilter getOporDocDueDate() {
        return oporDocDueDate;
    }

    public Optional<InstantFilter> optionalOporDocDueDate() {
        return Optional.ofNullable(oporDocDueDate);
    }

    public InstantFilter oporDocDueDate() {
        if (oporDocDueDate == null) {
            setOporDocDueDate(new InstantFilter());
        }
        return oporDocDueDate;
    }

    public void setOporDocDueDate(InstantFilter oporDocDueDate) {
        this.oporDocDueDate = oporDocDueDate;
    }

    public StringFilter getOporDocEntry() {
        return oporDocEntry;
    }

    public Optional<StringFilter> optionalOporDocEntry() {
        return Optional.ofNullable(oporDocEntry);
    }

    public StringFilter oporDocEntry() {
        if (oporDocEntry == null) {
            setOporDocEntry(new StringFilter());
        }
        return oporDocEntry;
    }

    public void setOporDocEntry(StringFilter oporDocEntry) {
        this.oporDocEntry = oporDocEntry;
    }

    public StringFilter getOporDocNum() {
        return oporDocNum;
    }

    public Optional<StringFilter> optionalOporDocNum() {
        return Optional.ofNullable(oporDocNum);
    }

    public StringFilter oporDocNum() {
        if (oporDocNum == null) {
            setOporDocNum(new StringFilter());
        }
        return oporDocNum;
    }

    public void setOporDocNum(StringFilter oporDocNum) {
        this.oporDocNum = oporDocNum;
    }

    public StringFilter getOporDocStatus() {
        return oporDocStatus;
    }

    public Optional<StringFilter> optionalOporDocStatus() {
        return Optional.ofNullable(oporDocStatus);
    }

    public StringFilter oporDocStatus() {
        if (oporDocStatus == null) {
            setOporDocStatus(new StringFilter());
        }
        return oporDocStatus;
    }

    public void setOporDocStatus(StringFilter oporDocStatus) {
        this.oporDocStatus = oporDocStatus;
    }

    public StringFilter getOporInvntSttus() {
        return oporInvntSttus;
    }

    public Optional<StringFilter> optionalOporInvntSttus() {
        return Optional.ofNullable(oporInvntSttus);
    }

    public StringFilter oporInvntSttus() {
        if (oporInvntSttus == null) {
            setOporInvntSttus(new StringFilter());
        }
        return oporInvntSttus;
    }

    public void setOporInvntSttus(StringFilter oporInvntSttus) {
        this.oporInvntSttus = oporInvntSttus;
    }

    public StringFilter getOporJrnlMemo() {
        return oporJrnlMemo;
    }

    public Optional<StringFilter> optionalOporJrnlMemo() {
        return Optional.ofNullable(oporJrnlMemo);
    }

    public StringFilter oporJrnlMemo() {
        if (oporJrnlMemo == null) {
            setOporJrnlMemo(new StringFilter());
        }
        return oporJrnlMemo;
    }

    public void setOporJrnlMemo(StringFilter oporJrnlMemo) {
        this.oporJrnlMemo = oporJrnlMemo;
    }

    public StringFilter getOporUCoAdd() {
        return oporUCoAdd;
    }

    public Optional<StringFilter> optionalOporUCoAdd() {
        return Optional.ofNullable(oporUCoAdd);
    }

    public StringFilter oporUCoAdd() {
        if (oporUCoAdd == null) {
            setOporUCoAdd(new StringFilter());
        }
        return oporUCoAdd;
    }

    public void setOporUCoAdd(StringFilter oporUCoAdd) {
        this.oporUCoAdd = oporUCoAdd;
    }

    public StringFilter getOporUCodeInv() {
        return oporUCodeInv;
    }

    public Optional<StringFilter> optionalOporUCodeInv() {
        return Optional.ofNullable(oporUCodeInv);
    }

    public StringFilter oporUCodeInv() {
        if (oporUCodeInv == null) {
            setOporUCodeInv(new StringFilter());
        }
        return oporUCodeInv;
    }

    public void setOporUCodeInv(StringFilter oporUCodeInv) {
        this.oporUCodeInv = oporUCodeInv;
    }

    public StringFilter getOporUCodeSerial() {
        return oporUCodeSerial;
    }

    public Optional<StringFilter> optionalOporUCodeSerial() {
        return Optional.ofNullable(oporUCodeSerial);
    }

    public StringFilter oporUCodeSerial() {
        if (oporUCodeSerial == null) {
            setOporUCodeSerial(new StringFilter());
        }
        return oporUCodeSerial;
    }

    public void setOporUCodeSerial(StringFilter oporUCodeSerial) {
        this.oporUCodeSerial = oporUCodeSerial;
    }

    public StringFilter getOporUContractDate() {
        return oporUContractDate;
    }

    public Optional<StringFilter> optionalOporUContractDate() {
        return Optional.ofNullable(oporUContractDate);
    }

    public StringFilter oporUContractDate() {
        if (oporUContractDate == null) {
            setOporUContractDate(new StringFilter());
        }
        return oporUContractDate;
    }

    public void setOporUContractDate(StringFilter oporUContractDate) {
        this.oporUContractDate = oporUContractDate;
    }

    public StringFilter getOporUDeclarePd() {
        return oporUDeclarePd;
    }

    public Optional<StringFilter> optionalOporUDeclarePd() {
        return Optional.ofNullable(oporUDeclarePd);
    }

    public StringFilter oporUDeclarePd() {
        if (oporUDeclarePd == null) {
            setOporUDeclarePd(new StringFilter());
        }
        return oporUDeclarePd;
    }

    public void setOporUDeclarePd(StringFilter oporUDeclarePd) {
        this.oporUDeclarePd = oporUDeclarePd;
    }

    public StringFilter getOporUDocNum() {
        return oporUDocNum;
    }

    public Optional<StringFilter> optionalOporUDocNum() {
        return Optional.ofNullable(oporUDocNum);
    }

    public StringFilter oporUDocNum() {
        if (oporUDocNum == null) {
            setOporUDocNum(new StringFilter());
        }
        return oporUDocNum;
    }

    public void setOporUDocNum(StringFilter oporUDocNum) {
        this.oporUDocNum = oporUDocNum;
    }

    public StringFilter getOporUInvCode() {
        return oporUInvCode;
    }

    public Optional<StringFilter> optionalOporUInvCode() {
        return Optional.ofNullable(oporUInvCode);
    }

    public StringFilter oporUInvCode() {
        if (oporUInvCode == null) {
            setOporUInvCode(new StringFilter());
        }
        return oporUInvCode;
    }

    public void setOporUInvCode(StringFilter oporUInvCode) {
        this.oporUInvCode = oporUInvCode;
    }

    public StringFilter getOporUInvCode2() {
        return oporUInvCode2;
    }

    public Optional<StringFilter> optionalOporUInvCode2() {
        return Optional.ofNullable(oporUInvCode2);
    }

    public StringFilter oporUInvCode2() {
        if (oporUInvCode2 == null) {
            setOporUInvCode2(new StringFilter());
        }
        return oporUInvCode2;
    }

    public void setOporUInvCode2(StringFilter oporUInvCode2) {
        this.oporUInvCode2 = oporUInvCode2;
    }

    public StringFilter getOporUInvSerial() {
        return oporUInvSerial;
    }

    public Optional<StringFilter> optionalOporUInvSerial() {
        return Optional.ofNullable(oporUInvSerial);
    }

    public StringFilter oporUInvSerial() {
        if (oporUInvSerial == null) {
            setOporUInvSerial(new StringFilter());
        }
        return oporUInvSerial;
    }

    public void setOporUInvSerial(StringFilter oporUInvSerial) {
        this.oporUInvSerial = oporUInvSerial;
    }

    public StringFilter getOporUPurNVGiao() {
        return oporUPurNVGiao;
    }

    public Optional<StringFilter> optionalOporUPurNVGiao() {
        return Optional.ofNullable(oporUPurNVGiao);
    }

    public StringFilter oporUPurNVGiao() {
        if (oporUPurNVGiao == null) {
            setOporUPurNVGiao(new StringFilter());
        }
        return oporUPurNVGiao;
    }

    public void setOporUPurNVGiao(StringFilter oporUPurNVGiao) {
        this.oporUPurNVGiao = oporUPurNVGiao;
    }

    public InstantFilter getOporUpdateDate() {
        return oporUpdateDate;
    }

    public Optional<InstantFilter> optionalOporUpdateDate() {
        return Optional.ofNullable(oporUpdateDate);
    }

    public InstantFilter oporUpdateDate() {
        if (oporUpdateDate == null) {
            setOporUpdateDate(new InstantFilter());
        }
        return oporUpdateDate;
    }

    public void setOporUpdateDate(InstantFilter oporUpdateDate) {
        this.oporUpdateDate = oporUpdateDate;
    }

    public StringFilter getOporUserSign() {
        return oporUserSign;
    }

    public Optional<StringFilter> optionalOporUserSign() {
        return Optional.ofNullable(oporUserSign);
    }

    public StringFilter oporUserSign() {
        if (oporUserSign == null) {
            setOporUserSign(new StringFilter());
        }
        return oporUserSign;
    }

    public void setOporUserSign(StringFilter oporUserSign) {
        this.oporUserSign = oporUserSign;
    }

    public InstantFilter getOporTaxDate() {
        return oporTaxDate;
    }

    public Optional<InstantFilter> optionalOporTaxDate() {
        return Optional.ofNullable(oporTaxDate);
    }

    public InstantFilter oporTaxDate() {
        if (oporTaxDate == null) {
            setOporTaxDate(new InstantFilter());
        }
        return oporTaxDate;
    }

    public void setOporTaxDate(InstantFilter oporTaxDate) {
        this.oporTaxDate = oporTaxDate;
    }

    public IntegerFilter getOporCntctCode() {
        return oporCntctCode;
    }

    public Optional<IntegerFilter> optionalOporCntctCode() {
        return Optional.ofNullable(oporCntctCode);
    }

    public IntegerFilter oporCntctCode() {
        if (oporCntctCode == null) {
            setOporCntctCode(new IntegerFilter());
        }
        return oporCntctCode;
    }

    public void setOporCntctCode(IntegerFilter oporCntctCode) {
        this.oporCntctCode = oporCntctCode;
    }

    public StringFilter getOporNumAtCard() {
        return oporNumAtCard;
    }

    public Optional<StringFilter> optionalOporNumAtCard() {
        return Optional.ofNullable(oporNumAtCard);
    }

    public StringFilter oporNumAtCard() {
        if (oporNumAtCard == null) {
            setOporNumAtCard(new StringFilter());
        }
        return oporNumAtCard;
    }

    public void setOporNumAtCard(StringFilter oporNumAtCard) {
        this.oporNumAtCard = oporNumAtCard;
    }

    public IntegerFilter getOporSlpCode() {
        return oporSlpCode;
    }

    public Optional<IntegerFilter> optionalOporSlpCode() {
        return Optional.ofNullable(oporSlpCode);
    }

    public IntegerFilter oporSlpCode() {
        if (oporSlpCode == null) {
            setOporSlpCode(new IntegerFilter());
        }
        return oporSlpCode;
    }

    public void setOporSlpCode(IntegerFilter oporSlpCode) {
        this.oporSlpCode = oporSlpCode;
    }

    public IntegerFilter getOporOwnerCode() {
        return oporOwnerCode;
    }

    public Optional<IntegerFilter> optionalOporOwnerCode() {
        return Optional.ofNullable(oporOwnerCode);
    }

    public IntegerFilter oporOwnerCode() {
        if (oporOwnerCode == null) {
            setOporOwnerCode(new IntegerFilter());
        }
        return oporOwnerCode;
    }

    public void setOporOwnerCode(IntegerFilter oporOwnerCode) {
        this.oporOwnerCode = oporOwnerCode;
    }

    public BigDecimalFilter getOporVatSum() {
        return oporVatSum;
    }

    public Optional<BigDecimalFilter> optionalOporVatSum() {
        return Optional.ofNullable(oporVatSum);
    }

    public BigDecimalFilter oporVatSum() {
        if (oporVatSum == null) {
            setOporVatSum(new BigDecimalFilter());
        }
        return oporVatSum;
    }

    public void setOporVatSum(BigDecimalFilter oporVatSum) {
        this.oporVatSum = oporVatSum;
    }

    public BigDecimalFilter getOporDocTotal() {
        return oporDocTotal;
    }

    public Optional<BigDecimalFilter> optionalOporDocTotal() {
        return Optional.ofNullable(oporDocTotal);
    }

    public BigDecimalFilter oporDocTotal() {
        if (oporDocTotal == null) {
            setOporDocTotal(new BigDecimalFilter());
        }
        return oporDocTotal;
    }

    public void setOporDocTotal(BigDecimalFilter oporDocTotal) {
        this.oporDocTotal = oporDocTotal;
    }

    public BigDecimalFilter getOporVatSumSy() {
        return oporVatSumSy;
    }

    public Optional<BigDecimalFilter> optionalOporVatSumSy() {
        return Optional.ofNullable(oporVatSumSy);
    }

    public BigDecimalFilter oporVatSumSy() {
        if (oporVatSumSy == null) {
            setOporVatSumSy(new BigDecimalFilter());
        }
        return oporVatSumSy;
    }

    public void setOporVatSumSy(BigDecimalFilter oporVatSumSy) {
        this.oporVatSumSy = oporVatSumSy;
    }

    public StringFilter getOporUHt() {
        return oporUHt;
    }

    public Optional<StringFilter> optionalOporUHt() {
        return Optional.ofNullable(oporUHt);
    }

    public StringFilter oporUHt() {
        if (oporUHt == null) {
            setOporUHt(new StringFilter());
        }
        return oporUHt;
    }

    public void setOporUHt(StringFilter oporUHt) {
        this.oporUHt = oporUHt;
    }

    public StringFilter getOporUPayment() {
        return oporUPayment;
    }

    public Optional<StringFilter> optionalOporUPayment() {
        return Optional.ofNullable(oporUPayment);
    }

    public StringFilter oporUPayment() {
        if (oporUPayment == null) {
            setOporUPayment(new StringFilter());
        }
        return oporUPayment;
    }

    public void setOporUPayment(StringFilter oporUPayment) {
        this.oporUPayment = oporUPayment;
    }

    public StringFilter getPor1BaseDocNum() {
        return por1BaseDocNum;
    }

    public Optional<StringFilter> optionalPor1BaseDocNum() {
        return Optional.ofNullable(por1BaseDocNum);
    }

    public StringFilter por1BaseDocNum() {
        if (por1BaseDocNum == null) {
            setPor1BaseDocNum(new StringFilter());
        }
        return por1BaseDocNum;
    }

    public void setPor1BaseDocNum(StringFilter por1BaseDocNum) {
        this.por1BaseDocNum = por1BaseDocNum;
    }

    public StringFilter getPor1BaseEntry() {
        return por1BaseEntry;
    }

    public Optional<StringFilter> optionalPor1BaseEntry() {
        return Optional.ofNullable(por1BaseEntry);
    }

    public StringFilter por1BaseEntry() {
        if (por1BaseEntry == null) {
            setPor1BaseEntry(new StringFilter());
        }
        return por1BaseEntry;
    }

    public void setPor1BaseEntry(StringFilter por1BaseEntry) {
        this.por1BaseEntry = por1BaseEntry;
    }

    public StringFilter getPor1BaseLine() {
        return por1BaseLine;
    }

    public Optional<StringFilter> optionalPor1BaseLine() {
        return Optional.ofNullable(por1BaseLine);
    }

    public StringFilter por1BaseLine() {
        if (por1BaseLine == null) {
            setPor1BaseLine(new StringFilter());
        }
        return por1BaseLine;
    }

    public void setPor1BaseLine(StringFilter por1BaseLine) {
        this.por1BaseLine = por1BaseLine;
    }

    public StringFilter getPor1BaseRef() {
        return por1BaseRef;
    }

    public Optional<StringFilter> optionalPor1BaseRef() {
        return Optional.ofNullable(por1BaseRef);
    }

    public StringFilter por1BaseRef() {
        if (por1BaseRef == null) {
            setPor1BaseRef(new StringFilter());
        }
        return por1BaseRef;
    }

    public void setPor1BaseRef(StringFilter por1BaseRef) {
        this.por1BaseRef = por1BaseRef;
    }

    public StringFilter getPor1Currency() {
        return por1Currency;
    }

    public Optional<StringFilter> optionalPor1Currency() {
        return Optional.ofNullable(por1Currency);
    }

    public StringFilter por1Currency() {
        if (por1Currency == null) {
            setPor1Currency(new StringFilter());
        }
        return por1Currency;
    }

    public void setPor1Currency(StringFilter por1Currency) {
        this.por1Currency = por1Currency;
    }

    public StringFilter getPor1DiscPrcnt() {
        return por1DiscPrcnt;
    }

    public Optional<StringFilter> optionalPor1DiscPrcnt() {
        return Optional.ofNullable(por1DiscPrcnt);
    }

    public StringFilter por1DiscPrcnt() {
        if (por1DiscPrcnt == null) {
            setPor1DiscPrcnt(new StringFilter());
        }
        return por1DiscPrcnt;
    }

    public void setPor1DiscPrcnt(StringFilter por1DiscPrcnt) {
        this.por1DiscPrcnt = por1DiscPrcnt;
    }

    public StringFilter getPoDocEntry() {
        return poDocEntry;
    }

    public Optional<StringFilter> optionalPoDocEntry() {
        return Optional.ofNullable(poDocEntry);
    }

    public StringFilter poDocEntry() {
        if (poDocEntry == null) {
            setPoDocEntry(new StringFilter());
        }
        return poDocEntry;
    }

    public void setPoDocEntry(StringFilter poDocEntry) {
        this.poDocEntry = poDocEntry;
    }

    public StringFilter getPor1Dscription() {
        return por1Dscription;
    }

    public Optional<StringFilter> optionalPor1Dscription() {
        return Optional.ofNullable(por1Dscription);
    }

    public StringFilter por1Dscription() {
        if (por1Dscription == null) {
            setPor1Dscription(new StringFilter());
        }
        return por1Dscription;
    }

    public void setPor1Dscription(StringFilter por1Dscription) {
        this.por1Dscription = por1Dscription;
    }

    public StringFilter getPor1ItemCode() {
        return por1ItemCode;
    }

    public Optional<StringFilter> optionalPor1ItemCode() {
        return Optional.ofNullable(por1ItemCode);
    }

    public StringFilter por1ItemCode() {
        if (por1ItemCode == null) {
            setPor1ItemCode(new StringFilter());
        }
        return por1ItemCode;
    }

    public void setPor1ItemCode(StringFilter por1ItemCode) {
        this.por1ItemCode = por1ItemCode;
    }

    public StringFilter getPor1LineNum() {
        return por1LineNum;
    }

    public Optional<StringFilter> optionalPor1LineNum() {
        return Optional.ofNullable(por1LineNum);
    }

    public StringFilter por1LineNum() {
        if (por1LineNum == null) {
            setPor1LineNum(new StringFilter());
        }
        return por1LineNum;
    }

    public void setPor1LineNum(StringFilter por1LineNum) {
        this.por1LineNum = por1LineNum;
    }

    public StringFilter getPor1LineStatus() {
        return por1LineStatus;
    }

    public Optional<StringFilter> optionalPor1LineStatus() {
        return Optional.ofNullable(por1LineStatus);
    }

    public StringFilter por1LineStatus() {
        if (por1LineStatus == null) {
            setPor1LineStatus(new StringFilter());
        }
        return por1LineStatus;
    }

    public void setPor1LineStatus(StringFilter por1LineStatus) {
        this.por1LineStatus = por1LineStatus;
    }

    public StringFilter getPor1LineVendor() {
        return por1LineVendor;
    }

    public Optional<StringFilter> optionalPor1LineVendor() {
        return Optional.ofNullable(por1LineVendor);
    }

    public StringFilter por1LineVendor() {
        if (por1LineVendor == null) {
            setPor1LineVendor(new StringFilter());
        }
        return por1LineVendor;
    }

    public void setPor1LineVendor(StringFilter por1LineVendor) {
        this.por1LineVendor = por1LineVendor;
    }

    public StringFilter getPor1OpenSumSys() {
        return por1OpenSumSys;
    }

    public Optional<StringFilter> optionalPor1OpenSumSys() {
        return Optional.ofNullable(por1OpenSumSys);
    }

    public StringFilter por1OpenSumSys() {
        if (por1OpenSumSys == null) {
            setPor1OpenSumSys(new StringFilter());
        }
        return por1OpenSumSys;
    }

    public void setPor1OpenSumSys(StringFilter por1OpenSumSys) {
        this.por1OpenSumSys = por1OpenSumSys;
    }

    public StringFilter getPor1Price() {
        return por1Price;
    }

    public Optional<StringFilter> optionalPor1Price() {
        return Optional.ofNullable(por1Price);
    }

    public StringFilter por1Price() {
        if (por1Price == null) {
            setPor1Price(new StringFilter());
        }
        return por1Price;
    }

    public void setPor1Price(StringFilter por1Price) {
        this.por1Price = por1Price;
    }

    public StringFilter getPor1Quantity() {
        return por1Quantity;
    }

    public Optional<StringFilter> optionalPor1Quantity() {
        return Optional.ofNullable(por1Quantity);
    }

    public StringFilter por1Quantity() {
        if (por1Quantity == null) {
            setPor1Quantity(new StringFilter());
        }
        return por1Quantity;
    }

    public void setPor1Quantity(StringFilter por1Quantity) {
        this.por1Quantity = por1Quantity;
    }

    public InstantFilter getPor1ShipDate() {
        return por1ShipDate;
    }

    public Optional<InstantFilter> optionalPor1ShipDate() {
        return Optional.ofNullable(por1ShipDate);
    }

    public InstantFilter por1ShipDate() {
        if (por1ShipDate == null) {
            setPor1ShipDate(new InstantFilter());
        }
        return por1ShipDate;
    }

    public void setPor1ShipDate(InstantFilter por1ShipDate) {
        this.por1ShipDate = por1ShipDate;
    }

    public StringFilter getPor1TotalFrgn() {
        return por1TotalFrgn;
    }

    public Optional<StringFilter> optionalPor1TotalFrgn() {
        return Optional.ofNullable(por1TotalFrgn);
    }

    public StringFilter por1TotalFrgn() {
        if (por1TotalFrgn == null) {
            setPor1TotalFrgn(new StringFilter());
        }
        return por1TotalFrgn;
    }

    public void setPor1TotalFrgn(StringFilter por1TotalFrgn) {
        this.por1TotalFrgn = por1TotalFrgn;
    }

    public StringFilter getPor1TotalSumsy() {
        return por1TotalSumsy;
    }

    public Optional<StringFilter> optionalPor1TotalSumsy() {
        return Optional.ofNullable(por1TotalSumsy);
    }

    public StringFilter por1TotalSumsy() {
        if (por1TotalSumsy == null) {
            setPor1TotalSumsy(new StringFilter());
        }
        return por1TotalSumsy;
    }

    public void setPor1TotalSumsy(StringFilter por1TotalSumsy) {
        this.por1TotalSumsy = por1TotalSumsy;
    }

    public StringFilter getPor1TrgetEntry() {
        return por1TrgetEntry;
    }

    public Optional<StringFilter> optionalPor1TrgetEntry() {
        return Optional.ofNullable(por1TrgetEntry);
    }

    public StringFilter por1TrgetEntry() {
        if (por1TrgetEntry == null) {
            setPor1TrgetEntry(new StringFilter());
        }
        return por1TrgetEntry;
    }

    public void setPor1TrgetEntry(StringFilter por1TrgetEntry) {
        this.por1TrgetEntry = por1TrgetEntry;
    }

    public StringFilter getPor1UMcode() {
        return por1UMcode;
    }

    public Optional<StringFilter> optionalPor1UMcode() {
        return Optional.ofNullable(por1UMcode);
    }

    public StringFilter por1UMcode() {
        if (por1UMcode == null) {
            setPor1UMcode(new StringFilter());
        }
        return por1UMcode;
    }

    public void setPor1UMcode(StringFilter por1UMcode) {
        this.por1UMcode = por1UMcode;
    }

    public StringFilter getPor1USo() {
        return por1USo;
    }

    public Optional<StringFilter> optionalPor1USo() {
        return Optional.ofNullable(por1USo);
    }

    public StringFilter por1USo() {
        if (por1USo == null) {
            setPor1USo(new StringFilter());
        }
        return por1USo;
    }

    public void setPor1USo(StringFilter por1USo) {
        this.por1USo = por1USo;
    }

    public StringFilter getPor1UTenkythuat() {
        return por1UTenkythuat;
    }

    public Optional<StringFilter> optionalPor1UTenkythuat() {
        return Optional.ofNullable(por1UTenkythuat);
    }

    public StringFilter por1UTenkythuat() {
        if (por1UTenkythuat == null) {
            setPor1UTenkythuat(new StringFilter());
        }
        return por1UTenkythuat;
    }

    public void setPor1UTenkythuat(StringFilter por1UTenkythuat) {
        this.por1UTenkythuat = por1UTenkythuat;
    }

    public StringFilter getPor1UnitMsr() {
        return por1UnitMsr;
    }

    public Optional<StringFilter> optionalPor1UnitMsr() {
        return Optional.ofNullable(por1UnitMsr);
    }

    public StringFilter por1UnitMsr() {
        if (por1UnitMsr == null) {
            setPor1UnitMsr(new StringFilter());
        }
        return por1UnitMsr;
    }

    public void setPor1UnitMsr(StringFilter por1UnitMsr) {
        this.por1UnitMsr = por1UnitMsr;
    }

    public StringFilter getPor1UOMCode() {
        return por1UOMCode;
    }

    public Optional<StringFilter> optionalPor1UOMCode() {
        return Optional.ofNullable(por1UOMCode);
    }

    public StringFilter por1UOMCode() {
        if (por1UOMCode == null) {
            setPor1UOMCode(new StringFilter());
        }
        return por1UOMCode;
    }

    public void setPor1UOMCode(StringFilter por1UOMCode) {
        this.por1UOMCode = por1UOMCode;
    }

    public StringFilter getPor1VatGroup() {
        return por1VatGroup;
    }

    public Optional<StringFilter> optionalPor1VatGroup() {
        return Optional.ofNullable(por1VatGroup);
    }

    public StringFilter por1VatGroup() {
        if (por1VatGroup == null) {
            setPor1VatGroup(new StringFilter());
        }
        return por1VatGroup;
    }

    public void setPor1VatGroup(StringFilter por1VatGroup) {
        this.por1VatGroup = por1VatGroup;
    }

    public BigDecimalFilter getPor1LineTotal() {
        return por1LineTotal;
    }

    public Optional<BigDecimalFilter> optionalPor1LineTotal() {
        return Optional.ofNullable(por1LineTotal);
    }

    public BigDecimalFilter por1LineTotal() {
        if (por1LineTotal == null) {
            setPor1LineTotal(new BigDecimalFilter());
        }
        return por1LineTotal;
    }

    public void setPor1LineTotal(BigDecimalFilter por1LineTotal) {
        this.por1LineTotal = por1LineTotal;
    }

    public BigDecimalFilter getPor1VatPrcnt() {
        return por1VatPrcnt;
    }

    public Optional<BigDecimalFilter> optionalPor1VatPrcnt() {
        return Optional.ofNullable(por1VatPrcnt);
    }

    public BigDecimalFilter por1VatPrcnt() {
        if (por1VatPrcnt == null) {
            setPor1VatPrcnt(new BigDecimalFilter());
        }
        return por1VatPrcnt;
    }

    public void setPor1VatPrcnt(BigDecimalFilter por1VatPrcnt) {
        this.por1VatPrcnt = por1VatPrcnt;
    }

    public BigDecimalFilter getPor1PriceAfVat() {
        return por1PriceAfVat;
    }

    public Optional<BigDecimalFilter> optionalPor1PriceAfVat() {
        return Optional.ofNullable(por1PriceAfVat);
    }

    public BigDecimalFilter por1PriceAfVat() {
        if (por1PriceAfVat == null) {
            setPor1PriceAfVat(new BigDecimalFilter());
        }
        return por1PriceAfVat;
    }

    public void setPor1PriceAfVat(BigDecimalFilter por1PriceAfVat) {
        this.por1PriceAfVat = por1PriceAfVat;
    }

    public StringFilter getPor1WhsCode() {
        return por1WhsCode;
    }

    public Optional<StringFilter> optionalPor1WhsCode() {
        return Optional.ofNullable(por1WhsCode);
    }

    public StringFilter por1WhsCode() {
        if (por1WhsCode == null) {
            setPor1WhsCode(new StringFilter());
        }
        return por1WhsCode;
    }

    public void setPor1WhsCode(StringFilter por1WhsCode) {
        this.por1WhsCode = por1WhsCode;
    }

    public StringFilter getPrMapPo() {
        return prMapPo;
    }

    public Optional<StringFilter> optionalPrMapPo() {
        return Optional.ofNullable(prMapPo);
    }

    public StringFilter prMapPo() {
        if (prMapPo == null) {
            setPrMapPo(new StringFilter());
        }
        return prMapPo;
    }

    public void setPrMapPo(StringFilter prMapPo) {
        this.prMapPo = prMapPo;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SapPoInfoCriteria that = (SapPoInfoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(oporBranch, that.oporBranch) &&
            Objects.equals(oporCanceled, that.oporCanceled) &&
            Objects.equals(oporCardCode, that.oporCardCode) &&
            Objects.equals(oporCardName, that.oporCardName) &&
            Objects.equals(oporComments, that.oporComments) &&
            Objects.equals(oporCreateDate, that.oporCreateDate) &&
            Objects.equals(oporDepartment, that.oporDepartment) &&
            Objects.equals(oporDocDate, that.oporDocDate) &&
            Objects.equals(oporDocDueDate, that.oporDocDueDate) &&
            Objects.equals(oporDocEntry, that.oporDocEntry) &&
            Objects.equals(oporDocNum, that.oporDocNum) &&
            Objects.equals(oporDocStatus, that.oporDocStatus) &&
            Objects.equals(oporInvntSttus, that.oporInvntSttus) &&
            Objects.equals(oporJrnlMemo, that.oporJrnlMemo) &&
            Objects.equals(oporUCoAdd, that.oporUCoAdd) &&
            Objects.equals(oporUCodeInv, that.oporUCodeInv) &&
            Objects.equals(oporUCodeSerial, that.oporUCodeSerial) &&
            Objects.equals(oporUContractDate, that.oporUContractDate) &&
            Objects.equals(oporUDeclarePd, that.oporUDeclarePd) &&
            Objects.equals(oporUDocNum, that.oporUDocNum) &&
            Objects.equals(oporUInvCode, that.oporUInvCode) &&
            Objects.equals(oporUInvCode2, that.oporUInvCode2) &&
            Objects.equals(oporUInvSerial, that.oporUInvSerial) &&
            Objects.equals(oporUPurNVGiao, that.oporUPurNVGiao) &&
            Objects.equals(oporUpdateDate, that.oporUpdateDate) &&
            Objects.equals(oporUserSign, that.oporUserSign) &&
            Objects.equals(oporTaxDate, that.oporTaxDate) &&
            Objects.equals(oporCntctCode, that.oporCntctCode) &&
            Objects.equals(oporNumAtCard, that.oporNumAtCard) &&
            Objects.equals(oporSlpCode, that.oporSlpCode) &&
            Objects.equals(oporOwnerCode, that.oporOwnerCode) &&
            Objects.equals(oporVatSum, that.oporVatSum) &&
            Objects.equals(oporDocTotal, that.oporDocTotal) &&
            Objects.equals(oporVatSumSy, that.oporVatSumSy) &&
            Objects.equals(oporUHt, that.oporUHt) &&
            Objects.equals(oporUPayment, that.oporUPayment) &&
            Objects.equals(por1BaseDocNum, that.por1BaseDocNum) &&
            Objects.equals(por1BaseEntry, that.por1BaseEntry) &&
            Objects.equals(por1BaseLine, that.por1BaseLine) &&
            Objects.equals(por1BaseRef, that.por1BaseRef) &&
            Objects.equals(por1Currency, that.por1Currency) &&
            Objects.equals(por1DiscPrcnt, that.por1DiscPrcnt) &&
            Objects.equals(poDocEntry, that.poDocEntry) &&
            Objects.equals(por1Dscription, that.por1Dscription) &&
            Objects.equals(por1ItemCode, that.por1ItemCode) &&
            Objects.equals(por1LineNum, that.por1LineNum) &&
            Objects.equals(por1LineStatus, that.por1LineStatus) &&
            Objects.equals(por1LineVendor, that.por1LineVendor) &&
            Objects.equals(por1OpenSumSys, that.por1OpenSumSys) &&
            Objects.equals(por1Price, that.por1Price) &&
            Objects.equals(por1Quantity, that.por1Quantity) &&
            Objects.equals(por1ShipDate, that.por1ShipDate) &&
            Objects.equals(por1TotalFrgn, that.por1TotalFrgn) &&
            Objects.equals(por1TotalSumsy, that.por1TotalSumsy) &&
            Objects.equals(por1TrgetEntry, that.por1TrgetEntry) &&
            Objects.equals(por1UMcode, that.por1UMcode) &&
            Objects.equals(por1USo, that.por1USo) &&
            Objects.equals(por1UTenkythuat, that.por1UTenkythuat) &&
            Objects.equals(por1UnitMsr, that.por1UnitMsr) &&
            Objects.equals(por1UOMCode, that.por1UOMCode) &&
            Objects.equals(por1VatGroup, that.por1VatGroup) &&
            Objects.equals(por1LineTotal, that.por1LineTotal) &&
            Objects.equals(por1VatPrcnt, that.por1VatPrcnt) &&
            Objects.equals(por1PriceAfVat, that.por1PriceAfVat) &&
            Objects.equals(por1WhsCode, that.por1WhsCode) &&
            Objects.equals(prMapPo, that.prMapPo) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            oporBranch,
            oporCanceled,
            oporCardCode,
            oporCardName,
            oporComments,
            oporCreateDate,
            oporDepartment,
            oporDocDate,
            oporDocDueDate,
            oporDocEntry,
            oporDocNum,
            oporDocStatus,
            oporInvntSttus,
            oporJrnlMemo,
            oporUCoAdd,
            oporUCodeInv,
            oporUCodeSerial,
            oporUContractDate,
            oporUDeclarePd,
            oporUDocNum,
            oporUInvCode,
            oporUInvCode2,
            oporUInvSerial,
            oporUPurNVGiao,
            oporUpdateDate,
            oporUserSign,
            oporTaxDate,
            oporCntctCode,
            oporNumAtCard,
            oporSlpCode,
            oporOwnerCode,
            oporVatSum,
            oporDocTotal,
            oporVatSumSy,
            oporUHt,
            oporUPayment,
            por1BaseDocNum,
            por1BaseEntry,
            por1BaseLine,
            por1BaseRef,
            por1Currency,
            por1DiscPrcnt,
            poDocEntry,
            por1Dscription,
            por1ItemCode,
            por1LineNum,
            por1LineStatus,
            por1LineVendor,
            por1OpenSumSys,
            por1Price,
            por1Quantity,
            por1ShipDate,
            por1TotalFrgn,
            por1TotalSumsy,
            por1TrgetEntry,
            por1UMcode,
            por1USo,
            por1UTenkythuat,
            por1UnitMsr,
            por1UOMCode,
            por1VatGroup,
            por1LineTotal,
            por1VatPrcnt,
            por1PriceAfVat,
            por1WhsCode,
            prMapPo,
            distinct
        );
    }

    // prettier-ignore
    @Override
  public String toString() {
    return "SapPoInfoCriteria{" +
        optionalId().map(f -> "id=" + f + ", ").orElse("") +
        optionalOporBranch().map(f -> "oporBranch=" + f + ", ").orElse("") +
        optionalOporCanceled().map(f -> "oporCanceled=" + f + ", ").orElse("") +
        optionalOporCardCode().map(f -> "oporCardCode=" + f + ", ").orElse("") +
        optionalOporCardName().map(f -> "oporCardName=" + f + ", ").orElse("") +
        optionalOporComments().map(f -> "oporComments=" + f + ", ").orElse("") +
        optionalOporCreateDate().map(f -> "oporCreateDate=" + f + ", ").orElse("") +
        optionalOporDepartment().map(f -> "oporDepartment=" + f + ", ").orElse("") +
        optionalOporDocDate().map(f -> "oporDocDate=" + f + ", ").orElse("") +
        optionalOporDocDueDate().map(f -> "oporDocDueDate=" + f + ", ").orElse("") +
        optionalOporDocEntry().map(f -> "oporDocEntry=" + f + ", ").orElse("") +
        optionalOporDocNum().map(f -> "oporDocNum=" + f + ", ").orElse("") +
        optionalOporDocStatus().map(f -> "oporDocStatus=" + f + ", ").orElse("") +
        optionalOporInvntSttus().map(f -> "oporInvntSttus=" + f + ", ").orElse("") +
        optionalOporJrnlMemo().map(f -> "oporJrnlMemo=" + f + ", ").orElse("") +
        optionalOporUCoAdd().map(f -> "oporUCoAdd=" + f + ", ").orElse("") +
        optionalOporUCodeInv().map(f -> "oporUCodeInv=" + f + ", ").orElse("") +
        optionalOporUCodeSerial().map(f -> "oporUCodeSerial=" + f + ", ").orElse("") +
        optionalOporUContractDate().map(f -> "oporUContractDate=" + f + ", ").orElse("") +
        optionalOporUDeclarePd().map(f -> "oporUDeclarePd=" + f + ", ").orElse("") +
        optionalOporUDocNum().map(f -> "oporUDocNum=" + f + ", ").orElse("") +
        optionalOporUInvCode().map(f -> "oporUInvCode=" + f + ", ").orElse("") +
        optionalOporUInvCode2().map(f -> "oporUInvCode2=" + f + ", ").orElse("") +
        optionalOporUInvSerial().map(f -> "oporUInvSerial=" + f + ", ").orElse("") +
        optionalOporUPurNVGiao().map(f -> "oporUPurNVGiao=" + f + ", ").orElse("") +
        optionalOporUpdateDate().map(f -> "oporUpdateDate=" + f + ", ").orElse("") +
        optionalOporUserSign().map(f -> "oporUserSign=" + f + ", ").orElse("") +
        optionalOporTaxDate().map(f -> "oporTaxDate=" + f + ", ").orElse("") +
        optionalOporCntctCode().map(f -> "oporCntctCode=" + f + ", ").orElse("") +
        optionalOporNumAtCard().map(f -> "oporNumAtCard=" + f + ", ").orElse("") +
        optionalOporSlpCode().map(f -> "oporSlpCode=" + f + ", ").orElse("") +
        optionalOporOwnerCode().map(f -> "oporOwnerCode=" + f + ", ").orElse("") +
        optionalOporVatSum().map(f -> "oporVatSum=" + f + ", ").orElse("") +
        optionalOporDocTotal().map(f -> "oporDocTotal=" + f + ", ").orElse("") +
        optionalOporVatSumSy().map(f -> "oporVatSumSy=" + f + ", ").orElse("") +
        optionalOporUHt().map(f -> "oporUHt=" + f + ", ").orElse("") +
        optionalOporUPayment().map(f -> "oporUPayment=" + f + ", ").orElse("") +
        optionalPor1BaseDocNum().map(f -> "por1BaseDocNum=" + f + ", ").orElse("") +
        optionalPor1BaseEntry().map(f -> "por1BaseEntry=" + f + ", ").orElse("") +
        optionalPor1BaseLine().map(f -> "por1BaseLine=" + f + ", ").orElse("") +
        optionalPor1BaseRef().map(f -> "por1BaseRef=" + f + ", ").orElse("") +
        optionalPor1Currency().map(f -> "por1Currency=" + f + ", ").orElse("") +
        optionalPor1DiscPrcnt().map(f -> "por1DiscPrcnt=" + f + ", ").orElse("") +
        optionalPoDocEntry().map(f -> "poDocEntry=" + f + ", ").orElse("") +
        optionalPor1Dscription().map(f -> "por1Dscription=" + f + ", ").orElse("") +
        optionalPor1ItemCode().map(f -> "por1ItemCode=" + f + ", ").orElse("") +
        optionalPor1LineNum().map(f -> "por1LineNum=" + f + ", ").orElse("") +
        optionalPor1LineStatus().map(f -> "por1LineStatus=" + f + ", ").orElse("") +
        optionalPor1LineVendor().map(f -> "por1LineVendor=" + f + ", ").orElse("") +
        optionalPor1OpenSumSys().map(f -> "por1OpenSumSys=" + f + ", ").orElse("") +
        optionalPor1Price().map(f -> "por1Price=" + f + ", ").orElse("") +
        optionalPor1Quantity().map(f -> "por1Quantity=" + f + ", ").orElse("") +
        optionalPor1ShipDate().map(f -> "por1ShipDate=" + f + ", ").orElse("") +
        optionalPor1TotalFrgn().map(f -> "por1TotalFrgn=" + f + ", ").orElse("") +
        optionalPor1TotalSumsy().map(f -> "por1TotalSumsy=" + f + ", ").orElse("") +
        optionalPor1TrgetEntry().map(f -> "por1TrgetEntry=" + f + ", ").orElse("") +
        optionalPor1UMcode().map(f -> "por1UMcode=" + f + ", ").orElse("") +
        optionalPor1USo().map(f -> "por1USo=" + f + ", ").orElse("") +
        optionalPor1UTenkythuat().map(f -> "por1UTenkythuat=" + f + ", ").orElse("") +
        optionalPor1UnitMsr().map(f -> "por1UnitMsr=" + f + ", ").orElse("") +
        optionalPor1UOMCode().map(f -> "por1UOMCode=" + f + ", ").orElse("") +
        optionalPor1VatGroup().map(f -> "por1VatGroup=" + f + ", ").orElse("") +
        optionalPor1LineTotal().map(f -> "por1LineTotal=" + f + ", ").orElse("") +
        optionalPor1VatPrcnt().map(f -> "por1VatPrcnt=" + f + ", ").orElse("") +
        optionalPor1PriceAfVat().map(f -> "por1PriceAfVat=" + f + ", ").orElse("") +
        optionalPor1WhsCode().map(f -> "por1WhsCode=" + f + ", ").orElse("") +
        optionalPrMapPo().map(f -> "prMapPo=" + f + ", ").orElse("") +
        optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
  }
}
