package com.mycompany.myapp.service;

import com.mycompany.panacimmc.domain.Location;
import com.mycompany.panacimmc.domain.LocationResponse;
import com.mycompany.panacimmc.repository.LocationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Autowired
    LocationRepository locationRepository;

    public List<LocationResponse> getAll() {
        return this.locationRepository.getFullLocation();
    }

    public String getLocationNameById(Long locationId) {
        System.out.println(("check functions. running :::: "));
        return locationRepository
            .findById(locationId)
            .map(Location::getLocationName)
            .orElse(null);
    }
}
