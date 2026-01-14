package viniciusDias1001.com.github.gerenciamento_de_tickets.service.impl;


import viniciusDias1001.com.github.gerenciamento_de_tickets.service.TicketHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.Ticket;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.TicketHistory;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.User;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.TicketHistoryResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.UserSummaryResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.ForbiddenException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.NotFoundException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.TicketHistoryRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.TicketRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.UserRepository;


import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketHistoryServiceImpl implements TicketHistoryService {


    private final TicketHistoryRepository historyRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketHistoryServiceImpl(TicketHistoryRepository historyRepository,
                                    TicketRepository ticketRepository,
                                    UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<TicketHistoryResponse> listByTicket(UUID requesterId, UUID ticketId, Pageable pageable) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);

        if (requester.getRole() == UserRole.CLIENT) {
            if (!ticket.getCreatedBy().getId().equals(requester.getId())) {
                throw new ForbiddenException("You are not allowed to view this ticket history");
            }
        }

        Page<TicketHistory> page = historyRepository.findByTicketIdOrderByCreatedAtDesc(ticketId, pageable);

        return page.map(this::toResponse);
    }

    private TicketHistoryResponse toResponse(TicketHistory h) {
        return new TicketHistoryResponse(
                h.getId(),
                h.getAction(),
                h.getFromStatus(),
                h.getToStatus(),
                h.getNotes(),
                h.getPerformedBy() == null ? null : toSummary(h.getPerformedBy()),
                h.getCreatedAt()
        );
    }

    private UserSummaryResponse toSummary(User u) {
        return new UserSummaryResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Ticket getTicketOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));
    }
}
