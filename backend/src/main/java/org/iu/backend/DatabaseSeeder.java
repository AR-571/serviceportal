package org.iu.backend;

import org.iu.backend.models.AppUser;
import org.iu.backend.models.Role;
import org.iu.backend.models.ServiceOffer;
import org.iu.backend.repositories.AppUserRepository;
import org.iu.backend.repositories.ServiceOfferRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final ServiceOfferRepository serviceOfferRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(AppUserRepository appUserRepository, ServiceOfferRepository serviceOfferRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.serviceOfferRepository = serviceOfferRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Prüfe, ob Benutzer bereits existieren
        if (appUserRepository.findAll().isEmpty()) {
            // Erstelle einen normalen USER
            AppUser normalUser = new AppUser();
            normalUser.setUsername("testuser");
            normalUser.setEmail("test@iu.de");
            normalUser.setPassword(passwordEncoder.encode("password123"));
            normalUser.setRole(Role.USER);
            appUserRepository.save(normalUser);
            System.out.println("✓ Testuser erstellt: testuser (USER)");

            // Erstelle einen ADMIN
            AppUser adminUser = new AppUser();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@iu.de");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(Role.ADMIN);
            appUserRepository.save(adminUser);
            System.out.println("✓ Adminuser erstellt: admin (ADMIN)");
        }

        // Erstelle Beispiel-Serviceangebote, wenn keine vorhanden sind
        if (serviceOfferRepository.findAll().isEmpty()) {
            ServiceOffer offer1 = new ServiceOffer();
            offer1.setTitle("IT-Support");
            offer1.setDescription("Technische Unterstützung und Problemlösung");
            serviceOfferRepository.save(offer1);

            ServiceOffer offer2 = new ServiceOffer();
            offer2.setTitle("Beratung");
            offer2.setDescription("Strategische und operative Beratung");
            serviceOfferRepository.save(offer2);

            System.out.println("✓ Serviceangebote erstellt");
        }
    }
}
