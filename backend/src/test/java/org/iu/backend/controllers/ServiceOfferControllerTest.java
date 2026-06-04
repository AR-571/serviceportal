package org.iu.backend.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.iu.backend.dto.ServiceOfferDTO;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceOfferControllerTest {

    @Mock
    private ServiceOfferRepository repository;

    @InjectMocks
    private ServiceOfferController controller;

    @Test
    void getAllReturnsOffersFromRepository() {
        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);
        offer.setTitle("IT-Support");
        offer.setDescription("Unterstützung bei technischen Problemen");

        when(repository.findAll()).thenReturn(List.of(offer));

        List<ServiceOfferDTO> result = controller.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("IT-Support");
        assertThat(result.get(0).getDescription()).isEqualTo("Unterstützung bei technischen Problemen");
    }

    @Test
    void getByIdReturnsOfferWhenExists() {
        ServiceOffer offer = new ServiceOffer();
        offer.setId(1L);
        offer.setTitle("IT-Support");
        offer.setDescription("Unterstützung bei technischen Problemen");

        when(repository.findById(1L)).thenReturn(Optional.of(offer));

        ResponseEntity<ServiceOfferDTO> result = controller.getById(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("IT-Support");
    }

    @Test
    void getByIdReturns404WhenNotExists() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ServiceOfferDTO> result = controller.getById(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createReturnsCreatedOffer() {
        ServiceOfferDTO dto = new ServiceOfferDTO();
        dto.setTitle("IT-Support");
        dto.setDescription("Unterstützung bei technischen Problemen");

        ServiceOffer savedOffer = new ServiceOffer();
        savedOffer.setId(1L);
        savedOffer.setTitle("IT-Support");
        savedOffer.setDescription("Unterstützung bei technischen Problemen");

        when(repository.save(org.mockito.ArgumentMatchers.any())).thenReturn(savedOffer);

        ResponseEntity<ServiceOfferDTO> result = controller.create(dto);

        assertThat(result.getStatusCode().value()).isEqualTo(201);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("IT-Support");
    }

    @Test
    void updateReturnsUpdatedOfferWhenExists() {
        ServiceOffer existingOffer = new ServiceOffer();
        existingOffer.setId(1L);
        existingOffer.setTitle("IT-Support");
        existingOffer.setDescription("Unterstützung bei technischen Problemen");

        ServiceOfferDTO dto = new ServiceOfferDTO();
        dto.setTitle("IT-Support Updated");
        dto.setDescription("Updated description");

        when(repository.findById(1L)).thenReturn(Optional.of(existingOffer));
        when(repository.save(existingOffer)).thenReturn(existingOffer);

        ResponseEntity<ServiceOfferDTO> result = controller.update(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void updateReturns404WhenNotExists() {
        ServiceOfferDTO dto = new ServiceOfferDTO();
        dto.setTitle("IT-Support Updated");

        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ServiceOfferDTO> result = controller.update(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void deleteReturns204WhenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> result = controller.delete(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteReturns404WhenNotExists() {
        when(repository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> result = controller.delete(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }
}
