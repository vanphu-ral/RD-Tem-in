package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.LocationService;
import com.mycompany.panacimmc.domain.AreaAreaResponse;
import com.mycompany.panacimmc.domain.Location;
import com.mycompany.panacimmc.domain.LocationResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    LocationService locationService;

    @GetMapping
    public List<LocationResponse> getAll() {
        System.out.println("Cong doan: 16");
        return this.locationService.getAll();
    }

    @GetMapping("/area")
    public List<AreaAreaResponse> getArea() {
        System.out.println("Cong doan: 17");
        return this.locationService.getArea();
    }
}
