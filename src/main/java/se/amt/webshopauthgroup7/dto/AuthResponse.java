package se.amt.webshopauthgroup7.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        long expiresIn,
        String username,
        List<String> roles
) {
}
