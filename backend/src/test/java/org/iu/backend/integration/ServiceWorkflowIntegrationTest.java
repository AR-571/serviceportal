package org.iu.backend.integration;

import org.iu.backend.models.AppUser;
import org.iu.backend.models.RequestStatus;
import org.iu.backend.models.Role;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.models.ServiceRequest;
import org.iu.backend.repositories.AppUserRepository;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.iu.backend.repositories.ServiceRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ServiceWorkflowIntegrationTest {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ServiceOfferRepository offerRepository;

    @Autowired
    private ServiceRequestRepository requestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AppUser testUser;
    private ServiceOffer testOffer;

    @BeforeEach
    void setUp() {
        // Clean up existing data to avoid conflicts
        requestRepository.deleteAll();
        offerRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user with unique username to avoid conflicts with DatabaseSeeder
        testUser = new AppUser();
        testUser.setUsername("integrationtestuser");
        testUser.setEmail("integrationtest@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);

        // Create test offer
        testOffer = new ServiceOffer();
        testOffer.setTitle("IT-Support");
        testOffer.setDescription("Unterstützung bei technischen Problemen");
        testOffer = offerRepository.save(testOffer);
    }

    @AfterEach
    void tearDown() {
        requestRepository.deleteAll();
        offerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCompleteServiceRequestWorkflow() {
        // Step 1: Create a service request
        ServiceRequest request = new ServiceRequest();
        request.setMessage("Ich brauche Hilfe bei meinem Computer");
        request.setStatus(RequestStatus.OPEN);
        request.setRequester(testUser);
        request.setServiceOffer(testOffer);
        ServiceRequest savedRequest = requestRepository.save(request);

        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getMessage()).isEqualTo("Ich brauche Hilfe bei meinem Computer");
        assertThat(savedRequest.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(savedRequest.getRequester().getId()).isEqualTo(testUser.getId());
        assertThat(savedRequest.getServiceOffer().getId()).isEqualTo(testOffer.getId());

        // Step 2: Retrieve the request
        ServiceRequest retrievedRequest = requestRepository.findById(savedRequest.getId()).orElse(null);
        assertThat(retrievedRequest).isNotNull();
        assertThat(retrievedRequest.getMessage()).isEqualTo("Ich brauche Hilfe bei meinem Computer");

        // Step 3: Update request status to IN_PROGRESS
        retrievedRequest.setStatus(RequestStatus.IN_PROGRESS);
        ServiceRequest updatedRequest = requestRepository.save(retrievedRequest);
        assertThat(updatedRequest.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);

        // Step 4: Update request status to CLOSED
        updatedRequest.setStatus(RequestStatus.CLOSED);
        ServiceRequest closedRequest = requestRepository.save(updatedRequest);
        assertThat(closedRequest.getStatus()).isEqualTo(RequestStatus.CLOSED);

        // Step 5: Verify all requests for the user
        var userRequests = requestRepository.findAll();
        assertThat(userRequests).hasSize(1);
        assertThat(userRequests.get(0).getStatus()).isEqualTo(RequestStatus.CLOSED);
    }

    @Test
    void testOfferManagementWorkflow() {
        // Step 1: Create multiple offers
        ServiceOffer offer1 = new ServiceOffer();
        offer1.setTitle("Beratung");
        offer1.setDescription("Professionelle Beratungsdienstleistungen");
        offer1 = offerRepository.save(offer1);

        ServiceOffer offer2 = new ServiceOffer();
        offer2.setTitle("Schulung");
        offer2.setDescription("IT-Schulungen für Mitarbeiter");
        offer2 = offerRepository.save(offer2);

        // Step 2: Retrieve all offers
        var allOffers = offerRepository.findAll();
        assertThat(allOffers).hasSize(3); // 2 new + 1 from setUp
        assertThat(allOffers).anyMatch(o -> o.getTitle().equals("Beratung"));
        assertThat(allOffers).anyMatch(o -> o.getTitle().equals("Schulung"));

        // Step 3: Update an offer
        offer1.setDescription("Aktualisierte Beschreibung für Beratung");
        ServiceOffer updatedOffer = offerRepository.save(offer1);
        assertThat(updatedOffer.getDescription()).isEqualTo("Aktualisierte Beschreibung für Beratung");

        // Step 4: Delete an offer
        offerRepository.deleteById(offer2.getId());
        var remainingOffers = offerRepository.findAll();
        assertThat(remainingOffers).hasSize(2);
        assertThat(remainingOffers).noneMatch(o -> o.getTitle().equals("Schulung"));
    }
}
