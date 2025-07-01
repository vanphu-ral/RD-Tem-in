package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.LocationService;
import com.mycompany.panacimmc.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {
    @Autowired
    LocationService locationService;
    @GetMapping()
    public List<Location> getAll(){
        return  this.locationService.getAll();
    }
}
