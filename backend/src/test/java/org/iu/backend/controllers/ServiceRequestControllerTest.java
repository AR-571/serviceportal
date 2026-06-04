package org.iu.backend.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.iu.backend.dto.ServiceRequestDTO;
import org.iu.backend.models.AppUser;
import org.iu.backend.models.RequestStatus;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.models.ServiceRequest;
import org.iu.backend.repositories.AppUserRepository;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.iu.backend.repositories.ServiceRequestRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceRequestControllerTest {

    @Mock
    private ServiceRequestRepository requestRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private ServiceOfferRepository offerRepository;

    @InjectMocks
    private ServiceRequestController controller;

    @Test
    void getAllReturnsAllRequests() {
        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setMessage("Test message");
        request.setStatus(RequestStatus.OPEN);

        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        request.setRequester(user);

        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);
        offer.setTitle("IT-Support");
        request.setServiceOffer(offer);

        when(requestRepository.findAll()).thenReturn(List.of(request));

        List<ServiceRequestDTO> result = controller.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("Test message");
    }

    @Test
    void createReturnsCreatedRequestWhenValid() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");

        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);
        offer.setTitle("IT-Support");

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setMessage("Test message");
        dto.setRequesterId(1L);
        dto.setServiceOfferId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(requestRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(201);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void createReturns400WhenRequesterIdIsNull() {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setMessage("Test message");
        dto.setRequesterId(null);
        dto.setServiceOfferId(1L);

        ResponseEntity<?> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("requesterId and serviceOfferId are required");
    }

    @Test
    void createReturns400WhenServiceOfferIdIsNull() {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setMessage("Test message");
        dto.setRequesterId(1L);
        dto.setServiceOfferId(null);

        ResponseEntity<?> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("requesterId and serviceOfferId are required");
    }

    @Test
    void createReturns400WhenRequesterNotFound() {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setMessage("Test message");
        dto.setRequesterId(1L);
        dto.setServiceOfferId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("requester not found");
    }

    @Test
    void createReturns400WhenServiceOfferNotFound() {
        AppUser user = new AppUser();
        user.setId(1L);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setMessage("Test message");
        dto.setRequesterId(1L);
        dto.setServiceOfferId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("serviceOffer not found");
    }

    @Test
    void createReturns400WhenStatusInvalid() {
        AppUser user = new AppUser();
        user.setId(1L);

        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setMessage("Test message");
        dto.setRequesterId(1L);
        dto.setServiceOfferId(1L);
        dto.setStatus("INVALID_STATUS");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        ResponseEntity<?> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("invalid status");
    }

    @Test
    void updateStatusReturnsUpdatedRequestWhenValid() {
        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.OPEN);

        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        request.setRequester(user);

        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);
        offer.setTitle("IT-Support");
        request.setServiceOffer(offer);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setStatus("IN_PROGRESS");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);

        ResponseEntity<?> result = controller.updateStatus(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void updateStatusReturns404WhenRequestNotFound() {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setStatus("IN_PROGRESS");

        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = controller.updateStatus(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void updateStatusReturns400WhenStatusIsNull() {
        ServiceRequest request = new ServiceRequest();
        request.setId(1L);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setStatus(null);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        ResponseEntity<?> result = controller.updateStatus(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("status is required");
    }

    @Test
    void updateStatusReturns400WhenStatusInvalid() {
        ServiceRequest request = new ServiceRequest();
        request.setId(1L);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setStatus("INVALID_STATUS");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        ResponseEntity<?> result = controller.updateStatus(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("invalid status");
    }
}
