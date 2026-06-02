package org.iu.backend.controllers;

import java.util.List;

import org.iu.backend.models.ServiceOffer;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/offers")
public class ServiceOfferController {

    private final ServiceOfferRepository repository;

    public ServiceOfferController(ServiceOfferRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServiceOffer> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<ServiceOffer> create(@RequestBody ServiceOffer offer) {
        ServiceOffer saved = repository.save(offer);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
