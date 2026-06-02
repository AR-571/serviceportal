package org.iu.backend.repositories;

import org.iu.backend.models.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
}
