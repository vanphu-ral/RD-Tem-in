package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.partner7.WhsRepository;
import com.mycompany.myapp.service.dto.WhsDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owhs")
public class WhsResource {

    private final WhsRepository whsRepository;

    public WhsResource(WhsRepository whsRepository) {
        this.whsRepository = whsRepository;
    }

    @GetMapping
    public ResponseEntity<List<WhsDTO>> getAll() {
        return ResponseEntity.ok(whsRepository.findAll());
    }
}
