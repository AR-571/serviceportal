package org.iu.backend.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.iu.backend.dto.AppUserDTO;
import org.iu.backend.models.AppUser;
import org.iu.backend.models.Role;
import org.iu.backend.repositories.AppUserRepository;
import org.iu.backend.security.AppUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppUserControllerTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AppUserController controller;

    @Test
    void getCurrentUserReturnsUserWhenAuthenticated() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        AppUserDetails userDetails = new AppUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        ResponseEntity<AppUserDTO> result = controller.getCurrentUser(authentication);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getUsername()).isEqualTo("testuser");
    }

    @Test
    void getCurrentUserReturns401WhenNotAuthenticated() {
        when(authentication.getPrincipal()).thenReturn(null);

        ResponseEntity<AppUserDTO> result = controller.getCurrentUser(authentication);

        assertThat(result.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void getCurrentUserReturns401WhenPrincipalNotAppUserDetails() {
        when(authentication.getPrincipal()).thenReturn("not AppUserDetails");

        ResponseEntity<AppUserDTO> result = controller.getCurrentUser(authentication);

        assertThat(result.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void getAllUsersReturnsUsersWithoutPasswords() {
        AppUser user1 = new AppUser();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setEmail("test@example.com");
        user1.setRole(Role.USER);

        AppUser user2 = new AppUser();
        user2.setId(2L);
        user2.setUsername("admin");
        user2.setEmail("admin@example.com");
        user2.setRole(Role.ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<AppUserDTO> result = controller.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(1).getUsername()).isEqualTo("admin");
    }

    @Test
    void updateRoleReturnsUpdatedUserWhenValid() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        AppUserDTO dto = new AppUserDTO();
        dto.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(2L);
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<?> result = controller.updateRole(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void updateRoleReturns404WhenUserNotExists() {
        AppUserDTO dto = new AppUserDTO();
        dto.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> result = controller.updateRole(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void updateRoleReturns400WhenRoleIsNull() {
        AppUserDTO dto = new AppUserDTO();
        dto.setRole(null);

        ResponseEntity<?> result = controller.updateRole(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("role is required");
    }

    @Test
    void updateRoleReturns400WhenRoleInvalid() {
        AppUserDTO dto = new AppUserDTO();
        dto.setRole("INVALID_ROLE");

        ResponseEntity<?> result = controller.updateRole(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("invalid role");
    }

    @Test
    void updateRoleReturns400WhenDemotingLastAdmin() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setRole(Role.ADMIN);

        AppUserDTO dto = new AppUserDTO();
        dto.setRole("USER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        ResponseEntity<?> result = controller.updateRole(1L, dto);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isEqualTo("cannot demote last admin");
    }
}
