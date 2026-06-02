package org.iu.backend.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.repositories.ServiceOfferRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOfferControllerTest {

    @Mock
    private ServiceOfferRepository repository;

    @InjectMocks
    private ServiceOfferController controller;

    @Test
    void getAllReturnsOffersFromRepository() {
        ServiceOffer offer = new ServiceOffer();
        offer.setTitle("IT-Support");
        offer.setDescription("Unterstützung bei technischen Problemen");

        when(repository.findAll()).thenReturn(List.of(offer));

        List<ServiceOffer> result = controller.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("IT-Support");
        assertThat(result.get(0).getDescription()).isEqualTo("Unterstützung bei technischen Problemen");
    }
}
