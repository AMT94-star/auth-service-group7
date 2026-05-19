package se.amt.webshopauthgroup7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.amt.webshopauthgroup7.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
