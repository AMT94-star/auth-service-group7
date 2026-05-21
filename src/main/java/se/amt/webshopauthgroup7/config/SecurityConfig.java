package se.amt.webshopauthgroup7.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
import se.amt.webshopauthgroup7.model.AppUser;
import se.amt.webshopauthgroup7.service.AppUserService;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String jwtIssuer;
    private final String jwtPublicKey;
    private final String jwtPrivateKey;
    private final String jwtKeyId;

    public SecurityConfig(
            @Value("${app.jwt.issuer}") String jwtIssuer,
            @Value("${app.jwt.public-key:}") String jwtPublicKey,
            @Value("${app.jwt.private-key:}") String jwtPrivateKey,
            @Value("${app.jwt.key-id}") String jwtKeyId
    ) {
        this.jwtIssuer = jwtIssuer;
        this.jwtPublicKey = jwtPublicKey;
        this.jwtPrivateKey = jwtPrivateKey;
        this.jwtKeyId = jwtKeyId;
    }

    @Bean
    public UserDetailsService userDetailsService(AppUserService appUserService) {
        return username -> {
            AppUser appUser = appUserService.findByUsername(username);

            return new User(
                    appUser.getUsername(),
                    appUser.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()))
            );
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/register", "/auth/login", "/auth/jwks").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public KeyPair keyPair() throws Exception {
        if (StringUtils.hasText(jwtPrivateKey) && StringUtils.hasText(jwtPublicKey)) {
            byte[] privateBytes = Base64.getMimeDecoder().decode(jwtPrivateKey);
            byte[] publicBytes = Base64.getMimeDecoder().decode(jwtPublicKey);
            //getMimeDecoder istället för getDecoder utifall det finns
            //radbrytningar i någon nyckel
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
            return new KeyPair(publicKey, privateKey);
        }
        throw new IllegalArgumentException("Privat eller publik nyckel för JWT saknas");
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(jwtKeyId)
                .build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}