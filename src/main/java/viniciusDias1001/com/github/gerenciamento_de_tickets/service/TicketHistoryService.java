package viniciusDias1001.com.github.gerenciamento_de_tickets.service;

import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.TicketHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TicketHistoryService {
    Page<TicketHistoryResponse> listByTicket(UUID requesterId, UUID ticketId, Pageable pageable);
}
