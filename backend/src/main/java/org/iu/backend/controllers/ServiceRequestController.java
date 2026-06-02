package org.iu.backend.controllers;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PostMapping;
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
    public List<ServiceRequest> getAll() {
        return requestRepository.findAll();
    }

    public static class CreateRequestDTO {
        public String message;
        public String status;
        public Long requesterId;
        public Long serviceOfferId;

        public CreateRequestDTO() {}

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getRequesterId() { return requesterId; }
        public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
        public Long getServiceOfferId() { return serviceOfferId; }
        public void setServiceOfferId(Long serviceOfferId) { this.serviceOfferId = serviceOfferId; }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequestDTO dto) {
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
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
