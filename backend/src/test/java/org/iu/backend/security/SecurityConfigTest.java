package org.iu.backend.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void securityConfigCanBeInstantiated() {
        SecurityConfig config = new SecurityConfig();
        assertThat(config).isNotNull();
    }

    @Test
    void passwordEncoderReturnsBCryptPasswordEncoder() {
        SecurityConfig config = new SecurityConfig();
        var encoder = config.passwordEncoder();
        assertThat(encoder).isNotNull();
    }

    @Test
    void passwordEncoderEncodesPassword() {
        SecurityConfig config = new SecurityConfig();
        var encoder = config.passwordEncoder();
        String rawPassword = "test123";
        String encodedPassword = encoder.encode(rawPassword);
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
