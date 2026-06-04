package org.iu.backend.repositories;

import org.iu.backend.models.AppUser;
import org.iu.backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    long countByRole(Role role);
}
