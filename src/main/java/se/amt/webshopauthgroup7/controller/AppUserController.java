package se.amt.webshopauthgroup7.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.amt.webshopauthgroup7.dto.RegisterRequest;
import se.amt.webshopauthgroup7.model.AppUser;
import se.amt.webshopauthgroup7.model.Role;
import se.amt.webshopauthgroup7.repository.AppUserRepository;

import java.util.List;

@RestController
@RequestMapping("/appusers")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<AppUser> getAppUsers() {
        return appUserRepository.findAll();
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public AppUser createAdmin(@RequestBody RegisterRequest registerRequest) {
        if (appUserRepository.existsByUsername(registerRequest.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already exists"
            );
        }

        AppUser admin = new AppUser();
        admin.setUsername(registerRequest.username());
        admin.setPassword(passwordEncoder.encode(registerRequest.password()));
        admin.setRole(Role.ADMIN);

        return appUserRepository.save(admin);
    }
}
