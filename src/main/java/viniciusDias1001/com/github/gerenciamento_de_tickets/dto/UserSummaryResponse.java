package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;

import java.util.UUID;

public record UserSummaryResponse(UUID id,
                                  String name,
                                  String email,
                                  UserRole role) {
}
