package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketHistoryResponse(UUID id,
                                    String action,
                                    TicketStatus fromStatus,
                                    TicketStatus toStatus,
                                    String notes,
                                    UserSummaryResponse performedBy,
                                    OffsetDateTime createdAt) {
}
