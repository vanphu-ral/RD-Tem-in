package com.mycompany.myapp.service;

import com.mycompany.panacimmc.domain.Location;
import com.mycompany.panacimmc.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    @Autowired
    LocationRepository locationRepository;
    public List<Location> getAll(){
        return this.locationRepository.getFullLocation();
    }

    public String getLocationNameById(Long locationId) {
        System.out.println(("check functions. running :::: "));
        return locationRepository.findById(locationId)
            .map(Location::getLocationName)
            .orElse(null);
    }
}
