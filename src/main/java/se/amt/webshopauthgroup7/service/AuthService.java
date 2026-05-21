package se.amt.webshopauthgroup7.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.amt.webshopauthgroup7.dto.AuthResponse;
import se.amt.webshopauthgroup7.dto.LoginRequest;
import se.amt.webshopauthgroup7.dto.RegisterRequest;
import se.amt.webshopauthgroup7.model.AppUser;
import se.amt.webshopauthgroup7.model.Role;
import se.amt.webshopauthgroup7.repository.AppUserRepository;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final KeyPair keyPair;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final String jwtIssuer;
    private final long jwtExpirationMinutes;
    private final String jwtKeyId;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtEncoder jwtEncoder,
                       KeyPair keyPair,
                       AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.jwt.issuer}") String jwtIssuer,
                       @Value("${app.jwt.expiration-minutes}") long jwtExpirationMinutes,
                       @Value("${app.jwt.key-id}") String jwtKeyId) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.keyPair = keyPair;
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.jwtIssuer = jwtIssuer;
        this.jwtExpirationMinutes = jwtExpirationMinutes;
        this.jwtKeyId = jwtKeyId;
    }

    public AuthResponse register(RegisterRequest request) {
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

        List<String> roles = List.of("ROLE_" + appUser.getRole().name());

        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtExpirationMinutes, ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(appUser.getUsername())
                .claim("roles", roles)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(jwtKeyId)
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        return new AuthResponse(accessToken,
                ChronoUnit.SECONDS.between(now, expiresAt),
                appUser.getUsername(), roles);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtExpirationMinutes, ChronoUnit.MINUTES);
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(principal.getUsername())
                .claim("roles", roles);
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(jwtKeyId)
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(jwsHeader, claims.build())
        ).getTokenValue();

        return new AuthResponse(
                accessToken,
                ChronoUnit.SECONDS.between(now, expiresAt),
                principal.getUsername(),
                roles
        );
    }

    public Map<String, Object> publicJwkSet() {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyID(jwtKeyId)
                .build();

        return new JWKSet(rsaKey).toJSONObject();
    }
}
