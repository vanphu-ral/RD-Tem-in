package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.SapPoInfo;
import com.mycompany.myapp.repository.partner6.SapPoInfoRepository;
import com.mycompany.myapp.service.criteria.SapPoInfoCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SapPoInfo} entities in the
 * database.
 * The main input is a {@link SapPoInfoCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SapPoInfo} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SapPoInfoQueryService extends QueryService<SapPoInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapPoInfoQueryService.class
    );

    private final SapPoInfoRepository sapPoInfoRepository;

    public SapPoInfoQueryService(SapPoInfoRepository sapPoInfoRepository) {
        this.sapPoInfoRepository = sapPoInfoRepository;
    }

    /**
     * Return a {@link Page} of {@link SapPoInfo} which matches the criteria from
     * the database.
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SapPoInfo> findByCriteria(
        SapPoInfoCriteria criteria,
        Pageable page
    ) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SapPoInfo> specification = createSpecification(
            criteria
        );
        return sapPoInfoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SapPoInfoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SapPoInfo> specification = createSpecification(
            criteria
        );
        return sapPoInfoRepository.count(specification);
    }

    /**
     * Function to convert {@link SapPoInfoCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SapPoInfo> createSpecification(
        SapPoInfoCriteria criteria
    ) {
        Specification<SapPoInfo> specification = Specification.where(null);
        if (criteria != null) {
            // Add distinct if specified
            if (Boolean.TRUE.equals(criteria.getDistinct())) {
                specification = specification.and(
                    distinct(criteria.getDistinct())
                );
            }

            // Add all other specifications using chained .and() calls
            specification = specification.and(
                buildRangeSpecification(criteria.getId(), SapPoInfo_.id)
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporBranch(),
                    SapPoInfo_.oporBranch
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporCanceled(),
                    SapPoInfo_.oporCanceled
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporCardCode(),
                    SapPoInfo_.oporCardCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporCardName(),
                    SapPoInfo_.oporCardName
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporComments(),
                    SapPoInfo_.oporComments
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporCreateDate(),
                    SapPoInfo_.oporCreateDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDepartment(),
                    SapPoInfo_.oporDepartment
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporDocDate(),
                    SapPoInfo_.oporDocDate
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporDocDueDate(),
                    SapPoInfo_.oporDocDueDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDocEntry(),
                    SapPoInfo_.oporDocEntry
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDocNum(),
                    SapPoInfo_.oporDocNum
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDocStatus(),
                    SapPoInfo_.oporDocStatus
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporInvntSttus(),
                    SapPoInfo_.oporInvntSttus
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporJrnlMemo(),
                    SapPoInfo_.oporJrnlMemo
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUCoAdd(),
                    SapPoInfo_.oporUCoAdd
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUCodeInv(),
                    SapPoInfo_.oporUCodeInv
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUCodeSerial(),
                    SapPoInfo_.oporUCodeSerial
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUContractDate(),
                    SapPoInfo_.oporUContractDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUDeclarePd(),
                    SapPoInfo_.oporUDeclarePd
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUDocNum(),
                    SapPoInfo_.oporUDocNum
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUInvCode(),
                    SapPoInfo_.oporUInvCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUInvCode2(),
                    SapPoInfo_.oporUInvCode2
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUInvSerial(),
                    SapPoInfo_.oporUInvSerial
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUPurNVGiao(),
                    SapPoInfo_.oporUPurNVGiao
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporUpdateDate(),
                    SapPoInfo_.oporUpdateDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUserSign(),
                    SapPoInfo_.oporUserSign
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporTaxDate(),
                    SapPoInfo_.oporTaxDate
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporCntctCode(),
                    SapPoInfo_.oporCntctCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporNumAtCard(),
                    SapPoInfo_.oporNumAtCard
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporSlpCode(),
                    SapPoInfo_.oporSlpCode
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporOwnerCode(),
                    SapPoInfo_.oporOwnerCode
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporVatSum(),
                    SapPoInfo_.oporVatSum
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporDocTotal(),
                    SapPoInfo_.oporDocTotal
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporVatSumSy(),
                    SapPoInfo_.oporVatSumSy
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUHt(),
                    SapPoInfo_.oporUHt
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUPayment(),
                    SapPoInfo_.oporUPayment
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1BaseDocNum(),
                    SapPoInfo_.por1BaseDocNum
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1BaseEntry(),
                    SapPoInfo_.por1BaseEntry
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1BaseLine(),
                    SapPoInfo_.por1BaseLine
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1BaseRef(),
                    SapPoInfo_.por1BaseRef
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1Currency(),
                    SapPoInfo_.por1Currency
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1DiscPrcnt(),
                    SapPoInfo_.por1DiscPrcnt
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPoDocEntry(),
                    SapPoInfo_.poDocEntry
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1Dscription(),
                    SapPoInfo_.por1Dscription
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1ItemCode(),
                    SapPoInfo_.por1ItemCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1LineNum(),
                    SapPoInfo_.por1LineNum
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1LineStatus(),
                    SapPoInfo_.por1LineStatus
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1LineVendor(),
                    SapPoInfo_.por1LineVendor
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1OpenSumSys(),
                    SapPoInfo_.por1OpenSumSys
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1Price(),
                    SapPoInfo_.por1Price
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1Quantity(),
                    SapPoInfo_.por1Quantity
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getPor1ShipDate(),
                    SapPoInfo_.por1ShipDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1TotalFrgn(),
                    SapPoInfo_.por1TotalFrgn
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1TotalSumsy(),
                    SapPoInfo_.por1TotalSumsy
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1TrgetEntry(),
                    SapPoInfo_.por1TrgetEntry
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1UMcode(),
                    SapPoInfo_.por1UMcode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1USo(),
                    SapPoInfo_.por1USo
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1UTenkythuat(),
                    SapPoInfo_.por1UTenkythuat
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1UnitMsr(),
                    SapPoInfo_.por1UnitMsr
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1UOMCode(),
                    SapPoInfo_.por1UOMCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1VatGroup(),
                    SapPoInfo_.por1VatGroup
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getPor1LineTotal(),
                    SapPoInfo_.por1LineTotal
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getPor1VatPrcnt(),
                    SapPoInfo_.por1VatPrcnt
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getPor1PriceAfVat(),
                    SapPoInfo_.por1PriceAfVat
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPor1WhsCode(),
                    SapPoInfo_.por1WhsCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getPrMapPo(),
                    SapPoInfo_.prMapPo
                )
            );
        }
        return specification;
    }
}
