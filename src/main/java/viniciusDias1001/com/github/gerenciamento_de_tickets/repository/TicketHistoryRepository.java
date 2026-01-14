package viniciusDias1001.com.github.gerenciamento_de_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.TicketHistory;
import java.util.UUID;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, UUID> {


    Page<TicketHistory> findByTicketIdOrderByCreatedAtDesc(
            UUID ticketId,
            Pageable pageable
    );
}
