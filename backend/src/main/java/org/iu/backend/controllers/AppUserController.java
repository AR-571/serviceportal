package org.iu.backend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.iu.backend.dto.AppUserDTO;
import org.iu.backend.models.AppUser;
import org.iu.backend.models.Role;
import org.iu.backend.repositories.AppUserRepository;
import org.iu.backend.security.AppUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserRepository userRepository;

    public AppUserController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<AppUserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails)) {
            return ResponseEntity.status(401).build();
        }

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        AppUser appUser = userDetails.getAppUser();

        return ResponseEntity.ok(toDTO(appUser));
    }

    @GetMapping
    public List<AppUserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody AppUserDTO dto) {
        if (dto.getRole() == null) {
            return ResponseEntity.badRequest().body("role is required");
        }

        try {
            Role newRole = Role.valueOf(dto.getRole());
            var userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            AppUser user = userOpt.get();

            // Prüfen: letzter Admin darf nicht degradiert werden
            if (user.getRole() == Role.ADMIN && newRole == Role.USER) {
                long adminCount = userRepository.countByRole(Role.ADMIN);
                if (adminCount <= 1) {
                    return ResponseEntity.badRequest().body("cannot demote last admin");
                }
            }

            user.setRole(newRole);
            AppUser updated = userRepository.save(user);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("invalid role");
        }
    }

    private AppUserDTO toDTO(AppUser user) {
        return new AppUserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
