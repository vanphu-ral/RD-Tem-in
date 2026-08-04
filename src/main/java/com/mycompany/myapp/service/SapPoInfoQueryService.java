package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SapOpor;
import com.mycompany.myapp.domain.SapOpor_;
import com.mycompany.myapp.repository.partner6.SapOporRepository;
import com.mycompany.myapp.service.criteria.SapPoInfoCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
public class SapPoInfoQueryService extends QueryService<SapOpor> {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapPoInfoQueryService.class
    );

    private final SapOporRepository sapOporRepository;

    public SapPoInfoQueryService(SapOporRepository sapOporRepository) {
        this.sapOporRepository = sapOporRepository;
    }

    @Transactional(readOnly = true)
    public Page<SapOpor> findByCriteria(
        SapPoInfoCriteria criteria,
        Pageable page
    ) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SapOpor> specification = createSpecification(
            criteria
        );
        return sapOporRepository.findAll(specification, page);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SapPoInfoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SapOpor> specification = createSpecification(
            criteria
        );
        return sapOporRepository.count(specification);
    }

    protected Specification<SapOpor> createSpecification(
        SapPoInfoCriteria criteria
    ) {
        Specification<SapOpor> specification = Specification.where(null);
        if (criteria != null) {
            if (Boolean.TRUE.equals(criteria.getDistinct())) {
                specification = specification.and(
                    distinct(criteria.getDistinct())
                );
            }

            specification = specification.and(
                buildRangeSpecification(criteria.getId(), SapOpor_.id)
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporBranch(),
                    SapOpor_.oporBranch
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporCanceled(),
                    SapOpor_.oporCanceled
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporCardCode(),
                    SapOpor_.oporCardCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporCardName(),
                    SapOpor_.oporCardName
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporComments(),
                    SapOpor_.oporComments
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporCreateDate(),
                    SapOpor_.oporCreateDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDepartment(),
                    SapOpor_.oporDepartment
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporDocDate(),
                    SapOpor_.oporDocDate
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporDocDueDate(),
                    SapOpor_.oporDocDueDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDocEntry(),
                    SapOpor_.oporDocEntry
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDocNum(),
                    SapOpor_.oporDocNum
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporDocStatus(),
                    SapOpor_.oporDocStatus
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporInvntSttus(),
                    SapOpor_.oporInvntSttus
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporJrnlMemo(),
                    SapOpor_.oporJrnlMemo
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUCoAdd(),
                    SapOpor_.oporUCoAdd
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUCodeInv(),
                    SapOpor_.oporUCodeInv
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUCodeSerial(),
                    SapOpor_.oporUCodeSerial
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUContractDate(),
                    SapOpor_.oporUContractDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUDeclarePd(),
                    SapOpor_.oporUDeclarePd
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUDocNum(),
                    SapOpor_.oporUDocNum
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUInvCode(),
                    SapOpor_.oporUInvCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUInvCode2(),
                    SapOpor_.oporUInvCode2
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUInvSerial(),
                    SapOpor_.oporUInvSerial
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUPurNVGiao(),
                    SapOpor_.oporUPurNVGiao
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporUpdateDate(),
                    SapOpor_.oporUpdateDate
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUserSign(),
                    SapOpor_.oporUserSign
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporTaxDate(),
                    SapOpor_.oporTaxDate
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporCntctCode(),
                    SapOpor_.oporCntctCode
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporNumAtCard(),
                    SapOpor_.oporNumAtCard
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporSlpCode(),
                    SapOpor_.oporSlpCode
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporOwnerCode(),
                    SapOpor_.oporOwnerCode
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporVatSum(),
                    SapOpor_.oporVatSum
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporDocTotal(),
                    SapOpor_.oporDocTotal
                )
            );
            specification = specification.and(
                buildRangeSpecification(
                    criteria.getOporVatSumSy(),
                    SapOpor_.oporVatSumSy
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUHt(),
                    SapOpor_.oporUHt
                )
            );
            specification = specification.and(
                buildStringSpecification(
                    criteria.getOporUPayment(),
                    SapOpor_.oporUPayment
                )
            );
        }
        return specification;
    }
}
