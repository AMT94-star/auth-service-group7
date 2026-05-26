package se.amt.webshopauthgroup7.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.amt.webshopauthgroup7.model.AppUser;
import se.amt.webshopauthgroup7.model.Role;
import se.amt.webshopauthgroup7.repository.AppUserRepository;

@Configuration
public class AdminConfig {

    @Bean
    CommandLineRunner createAdmin(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword) {
        //när appen startar returna
        return args -> {
            if (!appUserRepository.existsByUsername(adminUsername)) {
                AppUser admin = new AppUser();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);

                appUserRepository.save(admin);
            }
        };
    }
}
