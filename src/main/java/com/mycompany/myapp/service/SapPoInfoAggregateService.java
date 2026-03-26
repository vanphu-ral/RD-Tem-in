package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO.PoDetailDTO;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO.PoInfoDTO;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing SapPoInfo and building PO Info response with details.
 */
@Service
@Transactional
public class SapPoInfoAggregateService {

    private final Logger log = LoggerFactory.getLogger(
        SapPoInfoAggregateService.class
    );

    private final SapPoInfoRepository sapPoInfoRepository;

    public SapPoInfoAggregateService(SapPoInfoRepository sapPoInfoRepository) {
        this.sapPoInfoRepository = sapPoInfoRepository;
    }

    /**
     * Get PO Info with details by OPOR_DocEntry.
     *
     * @param oporDocEntry the OPOR_DocEntry to search for
     * @return PoInfoResponseDTO containing poInfo and list of poDetails
     */
    @Transactional(readOnly = true)
    public PoInfoResponseDTO getPoInfoByOporDocEntry(String oporDocEntry) {
        log.debug("Request to get PO Info by OPOR_DocEntry : {}", oporDocEntry);

        List<SapPoInfo> sapPoInfoList = sapPoInfoRepository.findByOporDocEntry(
            oporDocEntry
        );

        if (sapPoInfoList == null || sapPoInfoList.isEmpty()) {
            return new PoInfoResponseDTO(null, new ArrayList<>());
        }

        // Get the first record for header info (all records should have same header)
        SapPoInfo firstRecord = sapPoInfoList.get(0);

        // Build PO Info (Header)
        PoInfoDTO poInfoDTO = mapToPoInfoDTO(firstRecord);

        // Build PO Details (Lines)
        List<PoDetailDTO> poDetailDTOList = new ArrayList<>();
        for (SapPoInfo sapPoInfo : sapPoInfoList) {
            PoDetailDTO poDetailDTO = mapToPoDetailDTO(sapPoInfo);
            poDetailDTOList.add(poDetailDTO);
        }

        return new PoInfoResponseDTO(poInfoDTO, poDetailDTOList);
    }

    /**
     * Map SapPoInfo entity to PoInfoDTO (Header information)
     */
    private PoInfoDTO mapToPoInfoDTO(SapPoInfo entity) {
        PoInfoDTO dto = new PoInfoDTO();

        dto.setId(entity.getId());
        dto.setOporBranch(entity.getOporBranch());
        dto.setOporCanceled(entity.getOporCanceled());
        dto.setOporCardCode(entity.getOporCardCode());
        dto.setOporCardName(entity.getOporCardName());
        dto.setOporComments(entity.getOporComments());
        dto.setOporCreateDate(entity.getOporCreateDate());
        dto.setOporDepartment(entity.getOporDepartment());
        dto.setOporDocDate(entity.getOporDocDate());
        dto.setOporDocDueDate(entity.getOporDocDueDate());
        dto.setOporDocEntry(entity.getOporDocEntry());
        dto.setOporDocNum(entity.getOporDocNum());
        dto.setOporDocStatus(entity.getOporDocStatus());
        dto.setOporInvntSttus(entity.getOporInvntSttus());
        dto.setOporJrnlMemo(entity.getOporJrnlMemo());
        dto.setOporUCoAdd(entity.getOporUCoAdd());
        dto.setOporUCodeInv(entity.getOporUCodeInv());
        dto.setOporUCodeSerial(entity.getOporUCodeSerial());
        dto.setOporUContractDate(entity.getOporUContractDate());
        dto.setOporUDeclarePd(entity.getOporUDeclarePd());
        dto.setOporUDocNum(entity.getOporUDocNum());
        dto.setOporUInvCode(entity.getOporUInvCode());
        dto.setOporUInvCode2(entity.getOporUInvCode2());
        dto.setOporUInvSerial(entity.getOporUInvSerial());
        dto.setOporUPurNVGiao(entity.getOporUPurNVGiao());
        dto.setOporUpdateDate(entity.getOporUpdateDate());
        dto.setOporUserSign(entity.getOporUserSign());
        dto.setOporTaxDate(entity.getOporTaxDate());
        dto.setOporCntctCode(entity.getOporCntctCode());
        dto.setOporNumAtCard(entity.getOporNumAtCard());
        dto.setOporSlpCode(entity.getOporSlpCode());
        dto.setOporOwnerCode(entity.getOporOwnerCode());
        dto.setOporVatSum(entity.getOporVatSum());
        dto.setOporDocTotal(entity.getOporDocTotal());
        dto.setOporVatSumSy(entity.getOporVatSumSy());
        dto.setOporUHt(entity.getOporUHt());
        dto.setOporUPayment(entity.getOporUPayment());
        dto.setPrMapPo(entity.getPrMapPo());

        return dto;
    }

    /**
     * Map SapPoInfo entity to PoDetailDTO (Line information)
     */
    private PoDetailDTO mapToPoDetailDTO(SapPoInfo entity) {
        PoDetailDTO dto = new PoDetailDTO();

        dto.setId(entity.getId());
        dto.setPor1BaseDocNum(entity.getPor1BaseDocNum());
        dto.setPor1BaseEntry(entity.getPor1BaseEntry());
        dto.setPor1BaseLine(entity.getPor1BaseLine());
        dto.setPor1BaseRef(entity.getPor1BaseRef());
        dto.setPor1Currency(entity.getPor1Currency());
        dto.setPor1DiscPrcnt(entity.getPor1DiscPrcnt());
        dto.setPoDocEntry(entity.getPoDocEntry());
        dto.setPor1Dscription(entity.getPor1Dscription());
        dto.setPor1ItemCode(entity.getPor1ItemCode());
        dto.setPor1LineNum(entity.getPor1LineNum());
        dto.setPor1LineStatus(entity.getPor1LineStatus());
        dto.setPor1LineVendor(entity.getPor1LineVendor());
        dto.setPor1OpenSumSys(entity.getPor1OpenSumSys());
        dto.setPor1Price(entity.getPor1Price());
        dto.setPor1Quantity(entity.getPor1Quantity());
        dto.setPor1ShipDate(entity.getPor1ShipDate());
        dto.setPor1TotalFrgn(entity.getPor1TotalFrgn());
        dto.setPor1TotalSumsy(entity.getPor1TotalSumsy());
        dto.setPor1TrgetEntry(entity.getPor1TrgetEntry());
        dto.setPor1UMcode(entity.getPor1UMcode());
        dto.setPor1USo(entity.getPor1USo());
        dto.setPor1UTenkythuat(entity.getPor1UTenkythuat());
        dto.setPor1UnitMsr(entity.getPor1UnitMsr());
        dto.setPor1UOMCode(entity.getPor1UOMCode());
        dto.setPor1VatGroup(entity.getPor1VatGroup());
        dto.setPor1LineTotal(entity.getPor1LineTotal());
        dto.setPor1VatPrcnt(entity.getPor1VatPrcnt());
        dto.setPor1PriceAfVat(entity.getPor1PriceAfVat());
        dto.setPor1WhsCode(entity.getPor1WhsCode());

        return dto;
    }
}
