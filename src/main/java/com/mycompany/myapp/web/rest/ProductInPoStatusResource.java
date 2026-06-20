package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ProductInPoStatusRepository;
import com.mycompany.myapp.service.dto.ProductInPoStatusDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/product-in-po-status")
@Transactional
public class ProductInPoStatusResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ProductInPoStatusResource.class
    );

    private static final String ENTITY_NAME = "productInPoStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductInPoStatusRepository productInPoStatusRepository;

    public ProductInPoStatusResource(
        ProductInPoStatusRepository productInPoStatusRepository
    ) {
        this.productInPoStatusRepository = productInPoStatusRepository;
    }

    @PostMapping
    public ResponseEntity<ProductInPoStatusDTO> createProductInPoStatus(
        @Valid @RequestBody ProductInPoStatusDTO productInPoStatusDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to save ProductInPoStatus : {}",
            productInPoStatusDTO
        );
        if (productInPoStatusDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new productInPoStatus cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }

        ProductInPoStatusDTO result = productInPoStatusRepository.insert(
            productInPoStatusDTO
        );
        return ResponseEntity.created(
            new URI("/api/product-in-po-status/" + result.getId())
        )
            .headers(
                HeaderUtil.createEntityCreationAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    result.getId().toString()
                )
            )
            .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductInPoStatusDTO> updateProductInPoStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductInPoStatusDTO productInPoStatusDTO
    ) throws URISyntaxException {
        LOG.debug(
            "REST request to update ProductInPoStatus : {}, {}",
            id,
            productInPoStatusDTO
        );
        if (productInPoStatusDTO.getId() == null) {
            throw new BadRequestAlertException(
                "Invalid id",
                ENTITY_NAME,
                "idnull"
            );
        }
        if (!Objects.equals(id, productInPoStatusDTO.getId())) {
            throw new BadRequestAlertException(
                "Invalid ID",
                ENTITY_NAME,
                "idinvalid"
            );
        }

        ProductInPoStatusDTO result = productInPoStatusRepository
            .update(productInPoStatusDTO)
            .orElseThrow(() ->
                new BadRequestAlertException(
                    "Entity not found",
                    ENTITY_NAME,
                    "idnotfound"
                )
            );

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    result.getId().toString()
                )
            )
            .body(result);
    }

    @GetMapping
    public ResponseEntity<List<ProductInPoStatusDTO>> getAllProductInPoStatuses(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String sapCode,
        @RequestParam(required = false) String userData5,
        @RequestParam(required = false) String whsCode,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ProductInPoStatus");
        Page<ProductInPoStatusDTO> page;
        if (StringUtils.hasText(sapCode)) {
            page = productInPoStatusRepository.findBySapCode(sapCode, pageable);
        } else if (StringUtils.hasText(userData5)) {
            page = productInPoStatusRepository.findByUserData5(
                userData5,
                pageable
            );
        } else if (StringUtils.hasText(whsCode)) {
            page = productInPoStatusRepository.findByWhsCode(whsCode, pageable);
        } else if (StringUtils.hasText(keyword)) {
            page = productInPoStatusRepository.search(keyword, pageable);
        } else {
            page = productInPoStatusRepository.findAll(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/sap-code/{sapCode}")
    public ResponseEntity<
        List<ProductInPoStatusDTO>
    > getProductInPoStatusesBySapCode(
        @PathVariable String sapCode,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to get ProductInPoStatus by sapCode : {}",
            sapCode
        );
        Page<ProductInPoStatusDTO> page =
            productInPoStatusRepository.findBySapCode(sapCode, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/user-data5/{userData5}")
    public List<ProductInPoStatusDTO> getProductInPoStatusesByUserData5(
        @PathVariable String userData5
    ) {
        LOG.debug(
            "REST request to get ProductInPoStatus by userData5 : {}",
            userData5
        );
        return productInPoStatusRepository.findByUserData5List(userData5);
    }

    @GetMapping("/list-request-create-tem/{listRequestCreateTemId}")
    public ResponseEntity<
        List<ProductInPoStatusDTO>
    > getProductInPoStatusesByListRequestCreateTemId(
        @PathVariable Long listRequestCreateTemId,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to get ProductInPoStatus by listRequestCreateTemId : {}",
            listRequestCreateTemId
        );
        Page<ProductInPoStatusDTO> page =
            productInPoStatusRepository.findByListRequestCreateTemId(
                listRequestCreateTemId,
                pageable
            );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/warehouse/{whsCode}")
    public ResponseEntity<
        List<ProductInPoStatusDTO>
    > getProductInPoStatusesByWhsCode(
        @PathVariable String whsCode,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to get ProductInPoStatus by whsCode : {}",
            whsCode
        );
        Page<ProductInPoStatusDTO> page =
            productInPoStatusRepository.findByWhsCode(whsCode, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductInPoStatusDTO> getProductInPoStatus(
        @PathVariable Long id
    ) {
        LOG.debug("REST request to get ProductInPoStatus : {}", id);
        return ResponseUtil.wrapOrNotFound(
            productInPoStatusRepository.findById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductInPoStatus(@PathVariable Long id) {
        LOG.debug("REST request to delete ProductInPoStatus : {}", id);
        boolean deleted = productInPoStatusRepository.deleteById(id);
        if (!deleted) {
            throw new BadRequestAlertException(
                "Entity not found",
                ENTITY_NAME,
                "idnotfound"
            );
        }

        return ResponseEntity.noContent()
            .headers(
                HeaderUtil.createEntityDeletionAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    id.toString()
                )
            )
            .build();
    }
}
