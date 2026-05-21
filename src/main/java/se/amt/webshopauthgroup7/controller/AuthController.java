package se.amt.webshopauthgroup7.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.amt.webshopauthgroup7.dto.AuthResponse;
import se.amt.webshopauthgroup7.dto.LoginRequest;
import se.amt.webshopauthgroup7.dto.RegisterRequest;
import se.amt.webshopauthgroup7.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jwks")
    public ResponseEntity<Map<String, Object>> jwks() {
        return ResponseEntity.ok(authService.publicJwkSet());
    }
}
