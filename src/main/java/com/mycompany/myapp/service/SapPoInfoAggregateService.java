package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SapOpor;
import com.mycompany.myapp.domain.SapPor1;
import com.mycompany.myapp.repository.partner6.SapOporRepository;
import com.mycompany.myapp.repository.partner6.SapPor1Repository;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO.PoDetailDTO;
import com.mycompany.myapp.service.dto.PoInfoResponseDTO.PoInfoDTO;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SapPoInfoAggregateService {

    private final Logger log = LoggerFactory.getLogger(
        SapPoInfoAggregateService.class
    );

    private final SapOporRepository sapOporRepository;
    private final SapPor1Repository sapPor1Repository;

    public SapPoInfoAggregateService(
        SapOporRepository sapOporRepository,
        SapPor1Repository sapPor1Repository
    ) {
        this.sapOporRepository = sapOporRepository;
        this.sapPor1Repository = sapPor1Repository;
    }

    @Transactional(readOnly = true)
    public PoInfoResponseDTO getPoInfoByOporDocEntry(String oporDocEntry) {
        log.debug("Request to get PO Info by OPOR_DocEntry : {}", oporDocEntry);

        List<SapOpor> sapPoInfoList = sapOporRepository.findByOporDocEntry(
            oporDocEntry
        );

        if (sapPoInfoList == null || sapPoInfoList.isEmpty()) {
            return new PoInfoResponseDTO(null, new ArrayList<>());
        }

        SapOpor firstRecord = sapPoInfoList.get(0);
        PoInfoDTO poInfoDTO = mapToPoInfoDTO(firstRecord);

        List<SapPor1> por1List = sapPor1Repository.findByDocEntry(oporDocEntry);

        List<PoDetailDTO> poDetailDTOList = new ArrayList<>();
        for (SapPor1 por1 : por1List) {
            PoDetailDTO poDetailDTO = mapToPoDetailDTO(por1);
            poDetailDTOList.add(poDetailDTO);
        }

        return new PoInfoResponseDTO(poInfoDTO, poDetailDTOList);
    }

    private PoInfoDTO mapToPoInfoDTO(SapOpor entity) {
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

        return dto;
    }

    private PoDetailDTO mapToPoDetailDTO(SapPor1 entity) {
        PoDetailDTO dto = new PoDetailDTO();

        dto.setId(entity.getId());
        dto.setPor1BaseDocNum(entity.getBaseDocNum());
        dto.setPor1BaseEntry(entity.getBaseEntry());
        dto.setPor1BaseLine(entity.getBaseLine());
        dto.setPor1BaseRef(entity.getBaseRef());
        dto.setPor1Currency(entity.getCurrency());
        dto.setPor1DiscPrcnt(entity.getDiscPrcnt());
        dto.setPoDocEntry(entity.getDocEntry());
        dto.setPor1Dscription(entity.getDscription());
        dto.setPor1ItemCode(entity.getItemCode());
        dto.setPor1LineNum(entity.getLineNum());
        dto.setPor1LineStatus(entity.getLineStatus());
        dto.setPor1LineVendor(entity.getLineVendor());
        dto.setPor1OpenSumSys(entity.getOpenSumSys());
        dto.setPor1Price(entity.getPrice());
        dto.setPor1Quantity(
            entity.getQuantity() == null
                ? null
                : entity.getQuantity().toString()
        );
        dto.setPor1ShipDate(entity.getShipDate());
        dto.setPor1TotalFrgn(
            entity.getTotalFrgn() == null
                ? null
                : String.valueOf(entity.getTotalFrgn())
        );
        dto.setPor1TotalSumsy(entity.getTotalSumSy());
        dto.setPor1TrgetEntry(entity.getTrgetEntry());
        dto.setPor1UMcode(entity.getUMCode());
        dto.setPor1USo(entity.getUSo());
        dto.setPor1UTenkythuat(entity.getUTenkythuat());
        dto.setPor1UnitMsr(entity.getUnitMsr());
        dto.setPor1UOMCode(entity.getUomCode());
        dto.setPor1VatGroup(entity.getVatGroup());
        dto.setPor1LineTotal(entity.getLineTotal());
        dto.setPor1VatPrcnt(entity.getVatPrcnt());
        dto.setPor1PriceAfVat(entity.getPriceAfVat());
        dto.setPor1WhsCode(entity.getWhsCode());

        return dto;
    }
}
