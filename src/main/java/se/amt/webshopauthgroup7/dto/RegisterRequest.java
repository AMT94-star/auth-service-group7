package se.amt.webshopauthgroup7.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String username,
        @NotBlank @Size(min = 8) String password
) {
}
