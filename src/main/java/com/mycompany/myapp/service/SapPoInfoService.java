package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.mycompany.myapp.domain.SapPoInfo}.
 */
@Service
@Transactional
public class SapPoInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapPoInfoService.class
    );

    private final SapPoInfoRepository sapPoInfoRepository;

    public SapPoInfoService(SapPoInfoRepository sapPoInfoRepository) {
        this.sapPoInfoRepository = sapPoInfoRepository;
    }

    /**
     * Save a sapPoInfo.
     *
     * @param sapPoInfo the entity to save.
     * @return the persisted entity.
     */
    public SapPoInfo save(SapPoInfo sapPoInfo) {
        LOG.debug("Request to save SapPoInfo : {}", sapPoInfo);
        return sapPoInfoRepository.save(sapPoInfo);
    }

    /**
     * Update a sapPoInfo.
     *
     * @param sapPoInfo the entity to save.
     * @return the persisted entity.
     */
    public SapPoInfo update(SapPoInfo sapPoInfo) {
        LOG.debug("Request to update SapPoInfo : {}", sapPoInfo);
        return sapPoInfoRepository.save(sapPoInfo);
    }

    /**
     * Partially update a sapPoInfo.
     *
     * @param sapPoInfo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SapPoInfo> partialUpdate(SapPoInfo sapPoInfo) {
        LOG.debug("Request to partially update SapPoInfo : {}", sapPoInfo);

        return sapPoInfoRepository
            .findById(sapPoInfo.getId())
            .map(existingSapPoInfo -> {
                if (sapPoInfo.getOporBranch() != null) {
                    existingSapPoInfo.setOporBranch(sapPoInfo.getOporBranch());
                }
                if (sapPoInfo.getOporCanceled() != null) {
                    existingSapPoInfo.setOporCanceled(
                        sapPoInfo.getOporCanceled()
                    );
                }
                if (sapPoInfo.getOporCardCode() != null) {
                    existingSapPoInfo.setOporCardCode(
                        sapPoInfo.getOporCardCode()
                    );
                }
                if (sapPoInfo.getOporCardName() != null) {
                    existingSapPoInfo.setOporCardName(
                        sapPoInfo.getOporCardName()
                    );
                }
                if (sapPoInfo.getOporComments() != null) {
                    existingSapPoInfo.setOporComments(
                        sapPoInfo.getOporComments()
                    );
                }
                if (sapPoInfo.getOporCreateDate() != null) {
                    existingSapPoInfo.setOporCreateDate(
                        sapPoInfo.getOporCreateDate()
                    );
                }
                if (sapPoInfo.getOporDepartment() != null) {
                    existingSapPoInfo.setOporDepartment(
                        sapPoInfo.getOporDepartment()
                    );
                }
                if (sapPoInfo.getOporDocDate() != null) {
                    existingSapPoInfo.setOporDocDate(
                        sapPoInfo.getOporDocDate()
                    );
                }
                if (sapPoInfo.getOporDocDueDate() != null) {
                    existingSapPoInfo.setOporDocDueDate(
                        sapPoInfo.getOporDocDueDate()
                    );
                }
                if (sapPoInfo.getOporDocEntry() != null) {
                    existingSapPoInfo.setOporDocEntry(
                        sapPoInfo.getOporDocEntry()
                    );
                }
                if (sapPoInfo.getOporDocNum() != null) {
                    existingSapPoInfo.setOporDocNum(sapPoInfo.getOporDocNum());
                }
                if (sapPoInfo.getOporDocStatus() != null) {
                    existingSapPoInfo.setOporDocStatus(
                        sapPoInfo.getOporDocStatus()
                    );
                }
                if (sapPoInfo.getOporInvntSttus() != null) {
                    existingSapPoInfo.setOporInvntSttus(
                        sapPoInfo.getOporInvntSttus()
                    );
                }
                if (sapPoInfo.getOporJrnlMemo() != null) {
                    existingSapPoInfo.setOporJrnlMemo(
                        sapPoInfo.getOporJrnlMemo()
                    );
                }
                if (sapPoInfo.getOporUCoAdd() != null) {
                    existingSapPoInfo.setOporUCoAdd(sapPoInfo.getOporUCoAdd());
                }
                if (sapPoInfo.getOporUCodeInv() != null) {
                    existingSapPoInfo.setOporUCodeInv(
                        sapPoInfo.getOporUCodeInv()
                    );
                }
                if (sapPoInfo.getOporUCodeSerial() != null) {
                    existingSapPoInfo.setOporUCodeSerial(
                        sapPoInfo.getOporUCodeSerial()
                    );
                }
                if (sapPoInfo.getOporUContractDate() != null) {
                    existingSapPoInfo.setOporUContractDate(
                        sapPoInfo.getOporUContractDate()
                    );
                }
                if (sapPoInfo.getOporUDeclarePd() != null) {
                    existingSapPoInfo.setOporUDeclarePd(
                        sapPoInfo.getOporUDeclarePd()
                    );
                }
                if (sapPoInfo.getOporUDocNum() != null) {
                    existingSapPoInfo.setOporUDocNum(
                        sapPoInfo.getOporUDocNum()
                    );
                }
                if (sapPoInfo.getOporUInvCode() != null) {
                    existingSapPoInfo.setOporUInvCode(
                        sapPoInfo.getOporUInvCode()
                    );
                }
                if (sapPoInfo.getOporUInvCode2() != null) {
                    existingSapPoInfo.setOporUInvCode2(
                        sapPoInfo.getOporUInvCode2()
                    );
                }
                if (sapPoInfo.getOporUInvSerial() != null) {
                    existingSapPoInfo.setOporUInvSerial(
                        sapPoInfo.getOporUInvSerial()
                    );
                }
                if (sapPoInfo.getOporUPurNVGiao() != null) {
                    existingSapPoInfo.setOporUPurNVGiao(
                        sapPoInfo.getOporUPurNVGiao()
                    );
                }
                if (sapPoInfo.getOporUpdateDate() != null) {
                    existingSapPoInfo.setOporUpdateDate(
                        sapPoInfo.getOporUpdateDate()
                    );
                }
                if (sapPoInfo.getOporUserSign() != null) {
                    existingSapPoInfo.setOporUserSign(
                        sapPoInfo.getOporUserSign()
                    );
                }
                if (sapPoInfo.getOporTaxDate() != null) {
                    existingSapPoInfo.setOporTaxDate(
                        sapPoInfo.getOporTaxDate()
                    );
                }
                if (sapPoInfo.getOporCntctCode() != null) {
                    existingSapPoInfo.setOporCntctCode(
                        sapPoInfo.getOporCntctCode()
                    );
                }
                if (sapPoInfo.getOporNumAtCard() != null) {
                    existingSapPoInfo.setOporNumAtCard(
                        sapPoInfo.getOporNumAtCard()
                    );
                }
                if (sapPoInfo.getOporSlpCode() != null) {
                    existingSapPoInfo.setOporSlpCode(
                        sapPoInfo.getOporSlpCode()
                    );
                }
                if (sapPoInfo.getOporOwnerCode() != null) {
                    existingSapPoInfo.setOporOwnerCode(
                        sapPoInfo.getOporOwnerCode()
                    );
                }
                if (sapPoInfo.getOporVatSum() != null) {
                    existingSapPoInfo.setOporVatSum(sapPoInfo.getOporVatSum());
                }
                if (sapPoInfo.getOporDocTotal() != null) {
                    existingSapPoInfo.setOporDocTotal(
                        sapPoInfo.getOporDocTotal()
                    );
                }
                if (sapPoInfo.getOporVatSumSy() != null) {
                    existingSapPoInfo.setOporVatSumSy(
                        sapPoInfo.getOporVatSumSy()
                    );
                }
                if (sapPoInfo.getOporUHt() != null) {
                    existingSapPoInfo.setOporUHt(sapPoInfo.getOporUHt());
                }
                if (sapPoInfo.getOporUPayment() != null) {
                    existingSapPoInfo.setOporUPayment(
                        sapPoInfo.getOporUPayment()
                    );
                }
                if (sapPoInfo.getPor1BaseDocNum() != null) {
                    existingSapPoInfo.setPor1BaseDocNum(
                        sapPoInfo.getPor1BaseDocNum()
                    );
                }
                if (sapPoInfo.getPor1BaseEntry() != null) {
                    existingSapPoInfo.setPor1BaseEntry(
                        sapPoInfo.getPor1BaseEntry()
                    );
                }
                if (sapPoInfo.getPor1BaseLine() != null) {
                    existingSapPoInfo.setPor1BaseLine(
                        sapPoInfo.getPor1BaseLine()
                    );
                }
                if (sapPoInfo.getPor1BaseRef() != null) {
                    existingSapPoInfo.setPor1BaseRef(
                        sapPoInfo.getPor1BaseRef()
                    );
                }
                if (sapPoInfo.getPor1Currency() != null) {
                    existingSapPoInfo.setPor1Currency(
                        sapPoInfo.getPor1Currency()
                    );
                }
                if (sapPoInfo.getPor1DiscPrcnt() != null) {
                    existingSapPoInfo.setPor1DiscPrcnt(
                        sapPoInfo.getPor1DiscPrcnt()
                    );
                }
                if (sapPoInfo.getPoDocEntry() != null) {
                    existingSapPoInfo.setPoDocEntry(sapPoInfo.getPoDocEntry());
                }
                if (sapPoInfo.getPor1Dscription() != null) {
                    existingSapPoInfo.setPor1Dscription(
                        sapPoInfo.getPor1Dscription()
                    );
                }
                if (sapPoInfo.getPor1ItemCode() != null) {
                    existingSapPoInfo.setPor1ItemCode(
                        sapPoInfo.getPor1ItemCode()
                    );
                }
                if (sapPoInfo.getPor1LineNum() != null) {
                    existingSapPoInfo.setPor1LineNum(
                        sapPoInfo.getPor1LineNum()
                    );
                }
                if (sapPoInfo.getPor1LineStatus() != null) {
                    existingSapPoInfo.setPor1LineStatus(
                        sapPoInfo.getPor1LineStatus()
                    );
                }
                if (sapPoInfo.getPor1LineVendor() != null) {
                    existingSapPoInfo.setPor1LineVendor(
                        sapPoInfo.getPor1LineVendor()
                    );
                }
                if (sapPoInfo.getPor1OpenSumSys() != null) {
                    existingSapPoInfo.setPor1OpenSumSys(
                        sapPoInfo.getPor1OpenSumSys()
                    );
                }
                if (sapPoInfo.getPor1Price() != null) {
                    existingSapPoInfo.setPor1Price(sapPoInfo.getPor1Price());
                }
                if (sapPoInfo.getPor1Quantity() != null) {
                    existingSapPoInfo.setPor1Quantity(
                        sapPoInfo.getPor1Quantity()
                    );
                }
                if (sapPoInfo.getPor1ShipDate() != null) {
                    existingSapPoInfo.setPor1ShipDate(
                        sapPoInfo.getPor1ShipDate()
                    );
                }
                if (sapPoInfo.getPor1TotalFrgn() != null) {
                    existingSapPoInfo.setPor1TotalFrgn(
                        sapPoInfo.getPor1TotalFrgn()
                    );
                }
                if (sapPoInfo.getPor1TotalSumsy() != null) {
                    existingSapPoInfo.setPor1TotalSumsy(
                        sapPoInfo.getPor1TotalSumsy()
                    );
                }
                if (sapPoInfo.getPor1TrgetEntry() != null) {
                    existingSapPoInfo.setPor1TrgetEntry(
                        sapPoInfo.getPor1TrgetEntry()
                    );
                }
                if (sapPoInfo.getPor1UMcode() != null) {
                    existingSapPoInfo.setPor1UMcode(sapPoInfo.getPor1UMcode());
                }
                if (sapPoInfo.getPor1USo() != null) {
                    existingSapPoInfo.setPor1USo(sapPoInfo.getPor1USo());
                }
                if (sapPoInfo.getPor1UTenkythuat() != null) {
                    existingSapPoInfo.setPor1UTenkythuat(
                        sapPoInfo.getPor1UTenkythuat()
                    );
                }
                if (sapPoInfo.getPor1UnitMsr() != null) {
                    existingSapPoInfo.setPor1UnitMsr(
                        sapPoInfo.getPor1UnitMsr()
                    );
                }
                if (sapPoInfo.getPor1UOMCode() != null) {
                    existingSapPoInfo.setPor1UOMCode(
                        sapPoInfo.getPor1UOMCode()
                    );
                }
                if (sapPoInfo.getPor1VatGroup() != null) {
                    existingSapPoInfo.setPor1VatGroup(
                        sapPoInfo.getPor1VatGroup()
                    );
                }
                if (sapPoInfo.getPor1LineTotal() != null) {
                    existingSapPoInfo.setPor1LineTotal(
                        sapPoInfo.getPor1LineTotal()
                    );
                }
                if (sapPoInfo.getPor1VatPrcnt() != null) {
                    existingSapPoInfo.setPor1VatPrcnt(
                        sapPoInfo.getPor1VatPrcnt()
                    );
                }
                if (sapPoInfo.getPor1PriceAfVat() != null) {
                    existingSapPoInfo.setPor1PriceAfVat(
                        sapPoInfo.getPor1PriceAfVat()
                    );
                }
                if (sapPoInfo.getPor1WhsCode() != null) {
                    existingSapPoInfo.setPor1WhsCode(
                        sapPoInfo.getPor1WhsCode()
                    );
                }
                if (sapPoInfo.getPrMapPo() != null) {
                    existingSapPoInfo.setPrMapPo(sapPoInfo.getPrMapPo());
                }

                return existingSapPoInfo;
            })
            .map(sapPoInfoRepository::save);
    }

    /**
     * Get one sapPoInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SapPoInfo> findOne(Long id) {
        LOG.debug("Request to get SapPoInfo : {}", id);
        return sapPoInfoRepository.findById(id);
    }

    /**
     * Delete the sapPoInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SapPoInfo : {}", id);
        sapPoInfoRepository.deleteById(id);
    }
}
