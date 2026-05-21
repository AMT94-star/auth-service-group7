package se.amt.webshopauthgroup7.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.amt.webshopauthgroup7.dto.RegisterRequest;
import se.amt.webshopauthgroup7.model.AppUser;
import se.amt.webshopauthgroup7.model.Role;
import se.amt.webshopauthgroup7.repository.AppUserRepository;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository,
                          PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + username
                ));
    }

    public void register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already exists"
            );
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.username());
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.USER);

        appUserRepository.save(appUser);
    }
}