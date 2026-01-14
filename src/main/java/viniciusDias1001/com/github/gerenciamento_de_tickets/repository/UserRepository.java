package viniciusDias1001.com.github.gerenciamento_de_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.User;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(UserRole role, Pageable pageable);
}
