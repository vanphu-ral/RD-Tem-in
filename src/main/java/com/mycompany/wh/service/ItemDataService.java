package com.mycompany.wh.service;

import com.mycompany.wh.domain.ItemData;
import com.mycompany.wh.repository.ItemDataRepository;
import com.mycompany.wh.service.dto.PlanningWorkOrderDTO;
import com.mycompany.wh.service.dto.PlanningWorkOrderPageResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Service Implementation for managing ItemData using partner4 datasource.
 */
@Service
@Transactional("partner4TransactionManager")
public class ItemDataService {

    private static final Logger LOG = LoggerFactory.getLogger(
        ItemDataService.class
    );

    private static final String EXTERNAL_API_URL =
        "http://192.168.68.81:8080/api/planningworkorder?woId={code}";

    private final ItemDataRepository itemDataRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ItemDataService(ItemDataRepository itemDataRepository) {
        this.itemDataRepository = itemDataRepository;
    }

    /**
     * Get one ItemData by ItemCode.
     *
     * @param itemCode the itemCode of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ItemData> findByItemCode(String itemCode) {
        LOG.debug("Request to get ItemData by ItemCode : {}", itemCode);
        return itemDataRepository.findByItemCode(itemCode);
    }

    /**
     * Get enriched planning work orders by calling external API, filtering by woId,
     * and enriching with ItemData.
     *
     * @param code the woId to filter by.
     * @return the filtered and enriched page response.
     */
    public PlanningWorkOrderPageResponse getEnrichedPlanningWorkOrders(
        String code
    ) {
        LOG.debug(
            "Request to get enriched planning work orders for code : {}",
            code
        );

        try {
            // Call external API with woId parameter
            PlanningWorkOrderPageResponse response = restTemplate.getForObject(
                EXTERNAL_API_URL,
                PlanningWorkOrderPageResponse.class,
                code
            );

            if (response != null && response.getContent() != null) {
                // Enrich each work order with ItemData
                for (PlanningWorkOrderDTO workOrder : response.getContent()) {
                    if (workOrder.getProductCode() != null) {
                        Optional<ItemData> itemData = findByItemCode(
                            workOrder.getProductCode()
                        );
                        if (itemData.isPresent()) {
                            // Replace productType with Itm_Gr_Name from ItemData
                            workOrder.setProductType(
                                itemData.get().getItmGrName()
                            );
                            // Check if productType is "Thành phẩm" and prepend "LED" to productCode
                            if (
                                "Thành phẩm".equals(workOrder.getProductType())
                            ) {
                                workOrder.setProductCode(
                                    "LED" + workOrder.getProductCode()
                                );
                            }
                        }
                    }
                }
            }

            return response;
        } catch (Exception e) {
            LOG.error("Error calling external API or enriching data", e);
            throw new RuntimeException(
                "Failed to get enriched planning work orders",
                e
            );
        }
    }
}
