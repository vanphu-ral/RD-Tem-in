package com.mycompany.whh.service;

import com.mycompany.whh.domain.ItemData;
import com.mycompany.whh.repository.ItemDataRepository;
import com.mycompany.whh.service.dto.PlanningWorkOrderDTO;
import com.mycompany.whh.service.dto.PlanningWorkOrderPageResponse;
import java.util.Optional;
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
        "http://192.168.68.81:8080/api/planningworkorder";

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
     * Get enriched planning work orders by calling external API and enriching with ItemData.
     *
     * @param code the code parameter (not used in current implementation).
     * @return the enriched page response.
     */
    public PlanningWorkOrderPageResponse getEnrichedPlanningWorkOrders(
        String code
    ) {
        LOG.debug(
            "Request to get enriched planning work orders for code : {}",
            code
        );

        try {
            // Call external API
            PlanningWorkOrderPageResponse response = restTemplate.getForObject(
                EXTERNAL_API_URL,
                PlanningWorkOrderPageResponse.class
            );

            if (response != null && response.getContent() != null) {
                // Enrich each work order with ItemData
                for (PlanningWorkOrderDTO workOrder : response.getContent()) {
                    if (workOrder.getProductCode() != null) {
                        Optional<ItemData> itemData = findByItemCode(
                            workOrder.getProductCode()
                        );
                        if (itemData.isPresent()) {
                            // Replace productType with U_IGroupName from ItemData
                            workOrder.setProductType(
                                itemData.get().getuIGroupName()
                            );
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
