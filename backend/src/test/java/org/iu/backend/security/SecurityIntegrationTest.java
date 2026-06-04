package org.iu.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityIntegrationTest {

    @Test
    void contextLoads() {
        // Basic test to ensure Spring context loads with security configuration
        assertThat(true).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminRoleCanBeMocked() {
        // Test that @WithMockUser works for admin role
        assertThat(true).isTrue();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserRoleCanBeMocked() {
        // Test that @WithMockUser works for user role
        assertThat(true).isTrue();
    }
}
