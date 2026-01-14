package viniciusDias1001.com.github.gerenciamento_de_tickets.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.*;

import java.util.UUID;

public interface TicketService {
    TicketResponse createTicket(UUID requesterId, CreateTicketRequest request);

    TicketResponse getTicketById(UUID requesterId, UUID ticketId);

    Page<TicketResponse> listTickets(UUID requesterId,
                                     TicketStatus status,
                                     TicketPriority priority,
                                     Pageable pageable);

    TicketResponse updateTicket(UUID requesterId, UUID ticketId, UpdateTicketRequest request);

    TicketResponse changeStatus(UUID requesterId, UUID ticketId, ChangeStatusRequest request);

    TicketResponse assignTicket(UUID requesterId, UUID ticketId, UUID techId);

    void deleteTicket(UUID requesterId, UUID ticketId);

    @Transactional(readOnly = true)
    Page<TicketHistoryResponse> getTicketHistory(UUID requesterId, UUID ticketId, Pageable pageable);
}
