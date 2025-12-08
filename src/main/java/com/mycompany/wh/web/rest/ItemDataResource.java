package com.mycompany.wh.web.rest;

import com.mycompany.wh.domain.ItemData;
import com.mycompany.wh.service.ItemDataService;
import com.mycompany.wh.service.dto.PlanningWorkOrderPageResponse;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing ItemData.
 */
@RestController
@RequestMapping("/api/item-data")
public class ItemDataResource {

    private static final Logger LOG = LoggerFactory.getLogger(
        ItemDataResource.class
    );

    private final ItemDataService itemDataService;

    public ItemDataResource(ItemDataService itemDataService) {
        this.itemDataService = itemDataService;
    }

    /**
     * {@code GET  /item-data/:itemCode} : get the "itemCode" itemData.
     *
     * @param itemCode the itemCode of the itemData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the itemData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{itemCode}")
    public ResponseEntity<ItemData> getItemData(
        @PathVariable("itemCode") String itemCode
    ) {
        LOG.debug("REST request to get ItemData by ItemCode : {}", itemCode);
        Optional<ItemData> itemData = itemDataService.findByItemCode(itemCode);
        return ResponseUtil.wrapOrNotFound(itemData);
    }

    /**
     * {@code GET  /item-data/planning-work-orders/:code} : get enriched planning
     * work orders.
     *
     * @param code the code parameter.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the enriched planning work orders page response.
     */
    @GetMapping("/planning-work-orders/{code}")
    public ResponseEntity<
        PlanningWorkOrderPageResponse
    > getEnrichedPlanningWorkOrders(@PathVariable("code") String code) {
        LOG.debug(
            "REST request to get enriched planning work orders for code : {}",
            code
        );
        PlanningWorkOrderPageResponse response =
            itemDataService.getEnrichedPlanningWorkOrders(code);
        return ResponseEntity.ok(response);
    }
}
