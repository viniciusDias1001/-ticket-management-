package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(UUID id,
                             String title,
                             String description,
                             TicketStatus status,
                             TicketPriority priority,
                             UserSummaryResponse createdBy,
                             UserSummaryResponse assignedTo,
                             OffsetDateTime createdAt,
                             OffsetDateTime updatedAt) {
}
