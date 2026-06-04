package org.iu.backend.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.iu.backend.dto.ServiceOfferDTO;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public List<ServiceOfferDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOfferDTO> getById(@PathVariable Long id) {
        Optional<ServiceOffer> offerOpt = repository.findById(id);
        if (offerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDTO(offerOpt.get()));
    }

    @PostMapping
    public ResponseEntity<ServiceOfferDTO> create(@Valid @RequestBody ServiceOfferDTO dto) {
        ServiceOffer offer = new ServiceOffer();
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        ServiceOffer saved = repository.save(offer);
        return new ResponseEntity<>(toDTO(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOfferDTO> update(@PathVariable Long id, @Valid @RequestBody ServiceOfferDTO dto) {
        Optional<ServiceOffer> offerOpt = repository.findById(id);
        if (offerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ServiceOffer offer = offerOpt.get();
        if (dto.getTitle() != null) {
            offer.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            offer.setDescription(dto.getDescription());
        }

        ServiceOffer updated = repository.save(offer);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ServiceOfferDTO toDTO(ServiceOffer offer) {
        return new ServiceOfferDTO(offer.getId(), offer.getTitle(), offer.getDescription());
    }
}
