package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.partner4.SapOitm;
import com.mycompany.myapp.repository.partner4.SapOitmRepository;
import com.mycompany.myapp.service.SapOitmService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.partner4.SapOitm}.
 */
@Service
@Transactional("partner4TransactionManager")
public class SapOitmServiceImpl implements SapOitmService {

    private static final Logger LOG = LoggerFactory.getLogger(
        SapOitmServiceImpl.class
    );

    private final SapOitmRepository sapOitmRepository;

    public SapOitmServiceImpl(SapOitmRepository sapOitmRepository) {
        this.sapOitmRepository = sapOitmRepository;
    }

    @Override
    public SapOitm save(SapOitm sapOitm) {
        LOG.debug("Request to save SapOitm : {}", sapOitm);
        return sapOitmRepository.save(sapOitm);
    }

    @Override
    public SapOitm update(SapOitm sapOitm) {
        LOG.debug("Request to update SapOitm : {}", sapOitm);
        return sapOitmRepository.save(sapOitm);
    }

    @Override
    public Optional<SapOitm> partialUpdate(SapOitm sapOitm) {
        LOG.debug("Request to partially update SapOitm : {}", sapOitm);

        return sapOitmRepository
            .findById(sapOitm.getId())
            .map(existingSapOitm -> {
                if (sapOitm.getItemCode() != null) {
                    existingSapOitm.setItemCode(sapOitm.getItemCode());
                }
                if (sapOitm.getItemName() != null) {
                    existingSapOitm.setItemName(sapOitm.getItemName());
                }
                if (sapOitm.getItmsGrpCod() != null) {
                    existingSapOitm.setItmsGrpCod(sapOitm.getItmsGrpCod());
                }
                if (sapOitm.getuPartNumber() != null) {
                    existingSapOitm.setuPartNumber(sapOitm.getuPartNumber());
                }

                return existingSapOitm;
            })
            .map(sapOitmRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SapOitm> findAll() {
        LOG.debug("Request to get all SapOitms");
        return sapOitmRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SapOitm> findOne(Long id) {
        LOG.debug("Request to get SapOitm : {}", id);
        return sapOitmRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SapOitm : {}", id);
        sapOitmRepository.deleteById(id);
    }
}
