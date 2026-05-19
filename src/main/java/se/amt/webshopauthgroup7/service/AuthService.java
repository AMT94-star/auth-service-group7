package se.amt.webshopauthgroup7.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.amt.webshopauthgroup7.dto.RegisterRequest;
import se.amt.webshopauthgroup7.model.Role;
import se.amt.webshopauthgroup7.model.User;
import se.amt.webshopauthgroup7.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest registerRequest) {
        //om user existerar kastas det
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("User already exists");
        }

        //nya användare får standardrollen user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }
}
