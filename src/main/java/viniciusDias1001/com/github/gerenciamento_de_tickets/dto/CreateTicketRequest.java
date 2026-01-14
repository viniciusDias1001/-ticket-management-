package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import jakarta.validation.constraints.NotBlank;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;

public record CreateTicketRequest(@NotBlank String title,
                                  @NotBlank String description,
                                  TicketPriority priority) {
}
