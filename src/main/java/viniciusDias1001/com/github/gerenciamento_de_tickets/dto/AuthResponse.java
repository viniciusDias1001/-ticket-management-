package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;

import java.util.UUID;

public record AuthResponse(String accessToken,
                           UUID userId,
                           String name,
                           String email,
                           UserRole role) {
}
