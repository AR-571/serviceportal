package org.iu.backend.controllers;

import org.iu.backend.models.AppUser;
import org.iu.backend.security.AppUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @GetMapping("/me")
    public ResponseEntity<AppUser> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails)) {
            return ResponseEntity.status(401).build();
        }

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        AppUser appUser = userDetails.getAppUser();

        return ResponseEntity.ok(appUser);
    }
}
