package se.amt.webshopauthgroup7.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.amt.webshopauthgroup7.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
