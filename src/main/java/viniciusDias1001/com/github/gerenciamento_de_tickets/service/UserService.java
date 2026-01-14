package viniciusDias1001.com.github.gerenciamento_de_tickets.service;


import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.UserSummaryResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface UserService {

    UserSummaryResponse getById(UUID requesterId, UUID userId);

    Page<UserSummaryResponse> list(UUID requesterId, UserRole role, Pageable pageable);

    void delete(UUID requesterId, UUID userId);
}
