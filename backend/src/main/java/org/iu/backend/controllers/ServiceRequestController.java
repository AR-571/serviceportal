package org.iu.backend.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.iu.backend.dto.ServiceRequestDTO;
import org.iu.backend.models.AppUser;
import org.iu.backend.models.RequestStatus;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.models.ServiceRequest;
import org.iu.backend.repositories.AppUserRepository;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.iu.backend.repositories.ServiceRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/requests")
public class ServiceRequestController {

    private final ServiceRequestRepository requestRepository;
    private final AppUserRepository userRepository;
    private final ServiceOfferRepository offerRepository;

    public ServiceRequestController(ServiceRequestRepository requestRepository,
                                    AppUserRepository userRepository,
                                    ServiceOfferRepository offerRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
    }

    @GetMapping
    public List<ServiceRequestDTO> getAll() {
        return requestRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ServiceRequestDTO dto) {
        if (dto.getRequesterId() == null || dto.getServiceOfferId() == null) {
            return ResponseEntity.badRequest().body("requesterId and serviceOfferId are required");
        }

        Optional<AppUser> userOpt = userRepository.findById(dto.getRequesterId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("requester not found");
        }

        Optional<ServiceOffer> offerOpt = offerRepository.findById(dto.getServiceOfferId());
        if (offerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("serviceOffer not found");
        }

        ServiceRequest req = new ServiceRequest();
        req.setMessage(dto.getMessage());
        try {
            req.setStatus(dto.getStatus() == null ? RequestStatus.OPEN : RequestStatus.valueOf(dto.getStatus()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("invalid status");
        }
        req.setRequester(userOpt.get());
        req.setServiceOffer(offerOpt.get());

        ServiceRequest saved = requestRepository.save(req);
        return new ResponseEntity<>(toDTO(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody ServiceRequestDTO dto) {
        Optional<ServiceRequest> reqOpt = requestRepository.findById(id);
        if (reqOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getStatus() == null) {
            return ResponseEntity.badRequest().body("status is required");
        }

        try {
            RequestStatus newStatus = RequestStatus.valueOf(dto.getStatus());
            ServiceRequest req = reqOpt.get();
            req.setStatus(newStatus);
            ServiceRequest updated = requestRepository.save(req);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("invalid status");
        }
    }

    private ServiceRequestDTO toDTO(ServiceRequest req) {
        return new ServiceRequestDTO(
                req.getId(),
                req.getMessage(),
                req.getStatus().name(),
                req.getRequester().getId(),
                req.getRequester().getUsername(),
                req.getServiceOffer().getId(),
                req.getServiceOffer().getTitle()
        );
    }
}
