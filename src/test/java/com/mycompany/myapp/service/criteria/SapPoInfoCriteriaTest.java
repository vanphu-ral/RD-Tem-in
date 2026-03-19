package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SapPoInfoCriteriaTest {

    @Test
    void newSapPoInfoCriteriaHasAllFiltersNullTest() {
        var sapPoInfoCriteria = new SapPoInfoCriteria();
        assertThat(sapPoInfoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void sapPoInfoCriteriaFluentMethodsCreatesFiltersTest() {
        var sapPoInfoCriteria = new SapPoInfoCriteria();

        setAllFilters(sapPoInfoCriteria);

        assertThat(sapPoInfoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void sapPoInfoCriteriaCopyCreatesNullFilterTest() {
        var sapPoInfoCriteria = new SapPoInfoCriteria();
        var copy = sapPoInfoCriteria.copy();

        assertThat(sapPoInfoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) ->
                        (a == null || a instanceof Boolean)
                            ? a == b
                            : (a != b && a.equals(b))
                    )
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria ->
                assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(sapPoInfoCriteria)
        );
    }

    @Test
    void sapPoInfoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sapPoInfoCriteria = new SapPoInfoCriteria();
        setAllFilters(sapPoInfoCriteria);

        var copy = sapPoInfoCriteria.copy();

        assertThat(sapPoInfoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) ->
                        (a == null || a instanceof Boolean)
                            ? a == b
                            : (a != b && a.equals(b))
                    )
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria ->
                assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(sapPoInfoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sapPoInfoCriteria = new SapPoInfoCriteria();

        assertThat(sapPoInfoCriteria).hasToString("SapPoInfoCriteria{}");
    }

    private static void setAllFilters(SapPoInfoCriteria sapPoInfoCriteria) {
        sapPoInfoCriteria.id();
        sapPoInfoCriteria.oporBranch();
        sapPoInfoCriteria.oporCanceled();
        sapPoInfoCriteria.oporCardCode();
        sapPoInfoCriteria.oporCardName();
        sapPoInfoCriteria.oporComments();
        sapPoInfoCriteria.oporCreateDate();
        sapPoInfoCriteria.oporDepartment();
        sapPoInfoCriteria.oporDocDate();
        sapPoInfoCriteria.oporDocDueDate();
        sapPoInfoCriteria.oporDocEntry();
        sapPoInfoCriteria.oporDocNum();
        sapPoInfoCriteria.oporDocStatus();
        sapPoInfoCriteria.oporInvntSttus();
        sapPoInfoCriteria.oporJrnlMemo();
        sapPoInfoCriteria.oporUCoAdd();
        sapPoInfoCriteria.oporUCodeInv();
        sapPoInfoCriteria.oporUCodeSerial();
        sapPoInfoCriteria.oporUContractDate();
        sapPoInfoCriteria.oporUDeclarePd();
        sapPoInfoCriteria.oporUDocNum();
        sapPoInfoCriteria.oporUInvCode();
        sapPoInfoCriteria.oporUInvCode2();
        sapPoInfoCriteria.oporUInvSerial();
        sapPoInfoCriteria.oporUPurNVGiao();
        sapPoInfoCriteria.oporUpdateDate();
        sapPoInfoCriteria.oporUserSign();
        sapPoInfoCriteria.oporTaxDate();
        sapPoInfoCriteria.oporCntctCode();
        sapPoInfoCriteria.oporNumAtCard();
        sapPoInfoCriteria.oporSlpCode();
        sapPoInfoCriteria.oporOwnerCode();
        sapPoInfoCriteria.oporVatSum();
        sapPoInfoCriteria.oporDocTotal();
        sapPoInfoCriteria.oporVatSumSy();
        sapPoInfoCriteria.oporUHt();
        sapPoInfoCriteria.oporUPayment();
        sapPoInfoCriteria.por1BaseDocNum();
        sapPoInfoCriteria.por1BaseEntry();
        sapPoInfoCriteria.por1BaseLine();
        sapPoInfoCriteria.por1BaseRef();
        sapPoInfoCriteria.por1Currency();
        sapPoInfoCriteria.por1DiscPrcnt();
        sapPoInfoCriteria.poDocEntry();
        sapPoInfoCriteria.por1Dscription();
        sapPoInfoCriteria.por1ItemCode();
        sapPoInfoCriteria.por1LineNum();
        sapPoInfoCriteria.por1LineStatus();
        sapPoInfoCriteria.por1LineVendor();
        sapPoInfoCriteria.por1OpenSumSys();
        sapPoInfoCriteria.por1Price();
        sapPoInfoCriteria.por1Quantity();
        sapPoInfoCriteria.por1ShipDate();
        sapPoInfoCriteria.por1TotalFrgn();
        sapPoInfoCriteria.por1TotalSumsy();
        sapPoInfoCriteria.por1TrgetEntry();
        sapPoInfoCriteria.por1UMcode();
        sapPoInfoCriteria.por1USo();
        sapPoInfoCriteria.por1UTenkythuat();
        sapPoInfoCriteria.por1UnitMsr();
        sapPoInfoCriteria.por1UOMCode();
        sapPoInfoCriteria.por1VatGroup();
        sapPoInfoCriteria.por1LineTotal();
        sapPoInfoCriteria.por1VatPrcnt();
        sapPoInfoCriteria.por1PriceAfVat();
        sapPoInfoCriteria.por1WhsCode();
        sapPoInfoCriteria.prMapPo();
        sapPoInfoCriteria.distinct();
    }

    private static Condition<SapPoInfoCriteria> criteriaFiltersAre(
        Function<Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOporBranch()) &&
                condition.apply(criteria.getOporCanceled()) &&
                condition.apply(criteria.getOporCardCode()) &&
                condition.apply(criteria.getOporCardName()) &&
                condition.apply(criteria.getOporComments()) &&
                condition.apply(criteria.getOporCreateDate()) &&
                condition.apply(criteria.getOporDepartment()) &&
                condition.apply(criteria.getOporDocDate()) &&
                condition.apply(criteria.getOporDocDueDate()) &&
                condition.apply(criteria.getOporDocEntry()) &&
                condition.apply(criteria.getOporDocNum()) &&
                condition.apply(criteria.getOporDocStatus()) &&
                condition.apply(criteria.getOporInvntSttus()) &&
                condition.apply(criteria.getOporJrnlMemo()) &&
                condition.apply(criteria.getOporUCoAdd()) &&
                condition.apply(criteria.getOporUCodeInv()) &&
                condition.apply(criteria.getOporUCodeSerial()) &&
                condition.apply(criteria.getOporUContractDate()) &&
                condition.apply(criteria.getOporUDeclarePd()) &&
                condition.apply(criteria.getOporUDocNum()) &&
                condition.apply(criteria.getOporUInvCode()) &&
                condition.apply(criteria.getOporUInvCode2()) &&
                condition.apply(criteria.getOporUInvSerial()) &&
                condition.apply(criteria.getOporUPurNVGiao()) &&
                condition.apply(criteria.getOporUpdateDate()) &&
                condition.apply(criteria.getOporUserSign()) &&
                condition.apply(criteria.getOporTaxDate()) &&
                condition.apply(criteria.getOporCntctCode()) &&
                condition.apply(criteria.getOporNumAtCard()) &&
                condition.apply(criteria.getOporSlpCode()) &&
                condition.apply(criteria.getOporOwnerCode()) &&
                condition.apply(criteria.getOporVatSum()) &&
                condition.apply(criteria.getOporDocTotal()) &&
                condition.apply(criteria.getOporVatSumSy()) &&
                condition.apply(criteria.getOporUHt()) &&
                condition.apply(criteria.getOporUPayment()) &&
                condition.apply(criteria.getPor1BaseDocNum()) &&
                condition.apply(criteria.getPor1BaseEntry()) &&
                condition.apply(criteria.getPor1BaseLine()) &&
                condition.apply(criteria.getPor1BaseRef()) &&
                condition.apply(criteria.getPor1Currency()) &&
                condition.apply(criteria.getPor1DiscPrcnt()) &&
                condition.apply(criteria.getPoDocEntry()) &&
                condition.apply(criteria.getPor1Dscription()) &&
                condition.apply(criteria.getPor1ItemCode()) &&
                condition.apply(criteria.getPor1LineNum()) &&
                condition.apply(criteria.getPor1LineStatus()) &&
                condition.apply(criteria.getPor1LineVendor()) &&
                condition.apply(criteria.getPor1OpenSumSys()) &&
                condition.apply(criteria.getPor1Price()) &&
                condition.apply(criteria.getPor1Quantity()) &&
                condition.apply(criteria.getPor1ShipDate()) &&
                condition.apply(criteria.getPor1TotalFrgn()) &&
                condition.apply(criteria.getPor1TotalSumsy()) &&
                condition.apply(criteria.getPor1TrgetEntry()) &&
                condition.apply(criteria.getPor1UMcode()) &&
                condition.apply(criteria.getPor1USo()) &&
                condition.apply(criteria.getPor1UTenkythuat()) &&
                condition.apply(criteria.getPor1UnitMsr()) &&
                condition.apply(criteria.getPor1UOMCode()) &&
                condition.apply(criteria.getPor1VatGroup()) &&
                condition.apply(criteria.getPor1LineTotal()) &&
                condition.apply(criteria.getPor1VatPrcnt()) &&
                condition.apply(criteria.getPor1PriceAfVat()) &&
                condition.apply(criteria.getPor1WhsCode()) &&
                condition.apply(criteria.getPrMapPo()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SapPoInfoCriteria> copyFiltersAre(
        SapPoInfoCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(
                    criteria.getOporBranch(),
                    copy.getOporBranch()
                ) &&
                condition.apply(
                    criteria.getOporCanceled(),
                    copy.getOporCanceled()
                ) &&
                condition.apply(
                    criteria.getOporCardCode(),
                    copy.getOporCardCode()
                ) &&
                condition.apply(
                    criteria.getOporCardName(),
                    copy.getOporCardName()
                ) &&
                condition.apply(
                    criteria.getOporComments(),
                    copy.getOporComments()
                ) &&
                condition.apply(
                    criteria.getOporCreateDate(),
                    copy.getOporCreateDate()
                ) &&
                condition.apply(
                    criteria.getOporDepartment(),
                    copy.getOporDepartment()
                ) &&
                condition.apply(
                    criteria.getOporDocDate(),
                    copy.getOporDocDate()
                ) &&
                condition.apply(
                    criteria.getOporDocDueDate(),
                    copy.getOporDocDueDate()
                ) &&
                condition.apply(
                    criteria.getOporDocEntry(),
                    copy.getOporDocEntry()
                ) &&
                condition.apply(
                    criteria.getOporDocNum(),
                    copy.getOporDocNum()
                ) &&
                condition.apply(
                    criteria.getOporDocStatus(),
                    copy.getOporDocStatus()
                ) &&
                condition.apply(
                    criteria.getOporInvntSttus(),
                    copy.getOporInvntSttus()
                ) &&
                condition.apply(
                    criteria.getOporJrnlMemo(),
                    copy.getOporJrnlMemo()
                ) &&
                condition.apply(
                    criteria.getOporUCoAdd(),
                    copy.getOporUCoAdd()
                ) &&
                condition.apply(
                    criteria.getOporUCodeInv(),
                    copy.getOporUCodeInv()
                ) &&
                condition.apply(
                    criteria.getOporUCodeSerial(),
                    copy.getOporUCodeSerial()
                ) &&
                condition.apply(
                    criteria.getOporUContractDate(),
                    copy.getOporUContractDate()
                ) &&
                condition.apply(
                    criteria.getOporUDeclarePd(),
                    copy.getOporUDeclarePd()
                ) &&
                condition.apply(
                    criteria.getOporUDocNum(),
                    copy.getOporUDocNum()
                ) &&
                condition.apply(
                    criteria.getOporUInvCode(),
                    copy.getOporUInvCode()
                ) &&
                condition.apply(
                    criteria.getOporUInvCode2(),
                    copy.getOporUInvCode2()
                ) &&
                condition.apply(
                    criteria.getOporUInvSerial(),
                    copy.getOporUInvSerial()
                ) &&
                condition.apply(
                    criteria.getOporUPurNVGiao(),
                    copy.getOporUPurNVGiao()
                ) &&
                condition.apply(
                    criteria.getOporUpdateDate(),
                    copy.getOporUpdateDate()
                ) &&
                condition.apply(
                    criteria.getOporUserSign(),
                    copy.getOporUserSign()
                ) &&
                condition.apply(
                    criteria.getOporTaxDate(),
                    copy.getOporTaxDate()
                ) &&
                condition.apply(
                    criteria.getOporCntctCode(),
                    copy.getOporCntctCode()
                ) &&
                condition.apply(
                    criteria.getOporNumAtCard(),
                    copy.getOporNumAtCard()
                ) &&
                condition.apply(
                    criteria.getOporSlpCode(),
                    copy.getOporSlpCode()
                ) &&
                condition.apply(
                    criteria.getOporOwnerCode(),
                    copy.getOporOwnerCode()
                ) &&
                condition.apply(
                    criteria.getOporVatSum(),
                    copy.getOporVatSum()
                ) &&
                condition.apply(
                    criteria.getOporDocTotal(),
                    copy.getOporDocTotal()
                ) &&
                condition.apply(
                    criteria.getOporVatSumSy(),
                    copy.getOporVatSumSy()
                ) &&
                condition.apply(criteria.getOporUHt(), copy.getOporUHt()) &&
                condition.apply(
                    criteria.getOporUPayment(),
                    copy.getOporUPayment()
                ) &&
                condition.apply(
                    criteria.getPor1BaseDocNum(),
                    copy.getPor1BaseDocNum()
                ) &&
                condition.apply(
                    criteria.getPor1BaseEntry(),
                    copy.getPor1BaseEntry()
                ) &&
                condition.apply(
                    criteria.getPor1BaseLine(),
                    copy.getPor1BaseLine()
                ) &&
                condition.apply(
                    criteria.getPor1BaseRef(),
                    copy.getPor1BaseRef()
                ) &&
                condition.apply(
                    criteria.getPor1Currency(),
                    copy.getPor1Currency()
                ) &&
                condition.apply(
                    criteria.getPor1DiscPrcnt(),
                    copy.getPor1DiscPrcnt()
                ) &&
                condition.apply(
                    criteria.getPoDocEntry(),
                    copy.getPoDocEntry()
                ) &&
                condition.apply(
                    criteria.getPor1Dscription(),
                    copy.getPor1Dscription()
                ) &&
                condition.apply(
                    criteria.getPor1ItemCode(),
                    copy.getPor1ItemCode()
                ) &&
                condition.apply(
                    criteria.getPor1LineNum(),
                    copy.getPor1LineNum()
                ) &&
                condition.apply(
                    criteria.getPor1LineStatus(),
                    copy.getPor1LineStatus()
                ) &&
                condition.apply(
                    criteria.getPor1LineVendor(),
                    copy.getPor1LineVendor()
                ) &&
                condition.apply(
                    criteria.getPor1OpenSumSys(),
                    copy.getPor1OpenSumSys()
                ) &&
                condition.apply(criteria.getPor1Price(), copy.getPor1Price()) &&
                condition.apply(
                    criteria.getPor1Quantity(),
                    copy.getPor1Quantity()
                ) &&
                condition.apply(
                    criteria.getPor1ShipDate(),
                    copy.getPor1ShipDate()
                ) &&
                condition.apply(
                    criteria.getPor1TotalFrgn(),
                    copy.getPor1TotalFrgn()
                ) &&
                condition.apply(
                    criteria.getPor1TotalSumsy(),
                    copy.getPor1TotalSumsy()
                ) &&
                condition.apply(
                    criteria.getPor1TrgetEntry(),
                    copy.getPor1TrgetEntry()
                ) &&
                condition.apply(
                    criteria.getPor1UMcode(),
                    copy.getPor1UMcode()
                ) &&
                condition.apply(criteria.getPor1USo(), copy.getPor1USo()) &&
                condition.apply(
                    criteria.getPor1UTenkythuat(),
                    copy.getPor1UTenkythuat()
                ) &&
                condition.apply(
                    criteria.getPor1UnitMsr(),
                    copy.getPor1UnitMsr()
                ) &&
                condition.apply(
                    criteria.getPor1UOMCode(),
                    copy.getPor1UOMCode()
                ) &&
                condition.apply(
                    criteria.getPor1VatGroup(),
                    copy.getPor1VatGroup()
                ) &&
                condition.apply(
                    criteria.getPor1LineTotal(),
                    copy.getPor1LineTotal()
                ) &&
                condition.apply(
                    criteria.getPor1VatPrcnt(),
                    copy.getPor1VatPrcnt()
                ) &&
                condition.apply(
                    criteria.getPor1PriceAfVat(),
                    copy.getPor1PriceAfVat()
                ) &&
                condition.apply(
                    criteria.getPor1WhsCode(),
                    copy.getPor1WhsCode()
                ) &&
                condition.apply(criteria.getPrMapPo(), copy.getPrMapPo()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
