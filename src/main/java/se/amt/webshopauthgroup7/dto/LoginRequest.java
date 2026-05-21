package se.amt.webshopauthgroup7.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String username,
        @NotBlank String password
) {
}
