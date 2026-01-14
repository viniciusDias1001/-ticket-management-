package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import jakarta.validation.constraints.NotNull;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;

public record ChangeStatusRequest(@NotNull TicketStatus status) {
}
