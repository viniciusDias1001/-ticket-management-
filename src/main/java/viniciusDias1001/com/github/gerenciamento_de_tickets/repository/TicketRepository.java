package viniciusDias1001.com.github.gerenciamento_de_tickets.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.Ticket;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Pagination
    Page<Ticket> findByCreatedById(UUID createdById, Pageable pageable);

    // TECH: // Pagination
    Page<Ticket> findByAssignedToId(UUID assignedToId, Pageable pageable);


    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);


    Page<Ticket> findByCreatedByIdAndStatus(UUID createdById, TicketStatus status, Pageable pageable);


}
