package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.AreaService;
import com.mycompany.myapp.service.dto.AreaDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Area.
 */
@RestController
@RequestMapping("/api")
public class AreaResource {

    private final Logger log = LoggerFactory.getLogger(AreaResource.class);

    private final AreaService areaService;

    public AreaResource(AreaService areaService) {
        this.areaService = areaService;
    }

    /**
     * GET /areas : get all areas.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of areas in body.
     */
    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        log.debug("REST request to get all Areas");
        List<AreaDTO> areas = areaService.findAll();
        return ResponseEntity.ok().body(areas);
    }
}
