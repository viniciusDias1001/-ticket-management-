package viniciusDias1001.com.github.gerenciamento_de_tickets.dto.mapper;

import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.Ticket;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.User;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.TicketResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.UserSummaryResponse;

public final class TicketMapper {

    private TicketMapper() {}

    public static TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                toUserSummary(t.getCreatedBy()),
                t.getAssignedTo() == null ? null : toUserSummary(t.getAssignedTo()),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }

    public static UserSummaryResponse toUserSummary(User u) {
        return new UserSummaryResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
