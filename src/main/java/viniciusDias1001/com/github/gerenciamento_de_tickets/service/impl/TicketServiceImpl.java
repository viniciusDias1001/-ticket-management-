package viniciusDias1001.com.github.gerenciamento_de_tickets.service.impl;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.Ticket;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.TicketHistory;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.User;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.mapper.TicketMapper;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.BusinessRuleException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.ForbiddenException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.NotFoundException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.TicketHistoryRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.TicketRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.UserRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.service.TicketService;

import java.util.UUID;


@Service
@Transactional
public class TicketServiceImpl  implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             TicketHistoryRepository ticketHistoryRepository,
                             UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketResponse createTicket(UUID requesterId, CreateTicketRequest request) {
        User requester = getUserOrThrow(requesterId);

        if (requester.getRole() != UserRole.CLIENT && requester.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only CLIENT can create tickets");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority() == null ? TicketPriority.MEDIUM : request.priority());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(requester);

        Ticket saved = ticketRepository.save(ticket);

        createHistory(saved, requester, null, TicketStatus.OPEN, "CREATED", "Ticket created");

        return TicketMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(UUID requesterId, UUID ticketId) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);

        validateReadAccess(requester, ticket);

        return TicketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponse> listTickets(UUID requesterId,
                                            TicketStatus status,
                                            TicketPriority priority,
                                            Pageable pageable) {
        User requester = getUserOrThrow(requesterId);


        if (requester.getRole() == UserRole.CLIENT) {
            if (status != null) {
                return ticketRepository
                        .findByCreatedByIdAndStatus(requester.getId(), status, pageable)
                        .map(TicketMapper::toResponse);
            }
            return ticketRepository
                    .findByCreatedById(requester.getId(), pageable)
                    .map(TicketMapper::toResponse);
        }


        if (status != null) {
            return ticketRepository.findByStatus(status, pageable).map(TicketMapper::toResponse);
        }
        if (priority != null) {
            return ticketRepository.findByPriority(priority, pageable).map(TicketMapper::toResponse);
        }

        return ticketRepository.findAll(pageable).map(TicketMapper::toResponse);
    }

    @Override
    public TicketResponse updateTicket(UUID requesterId, UUID ticketId, UpdateTicketRequest request) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);

        validateWriteAccess(requester, ticket);
        validateNotDone(ticket);

        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority() == null ? ticket.getPriority() : request.priority());

        Ticket saved = ticketRepository.save(ticket);

        createHistory(saved, requester, saved.getStatus(), saved.getStatus(), "UPDATED", "Ticket updated");

        return TicketMapper.toResponse(saved);
    }

    @Override
    public TicketResponse changeStatus(UUID requesterId, UUID ticketId, ChangeStatusRequest request) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);


        if (requester.getRole() == UserRole.CLIENT) {
            throw new ForbiddenException("CLIENT cannot change ticket status");
        }

        validateNotDone(ticket);

        TicketStatus from = ticket.getStatus();
        TicketStatus to = request.status();

        if (from == to) {
            throw new BusinessRuleException("New status must be different from current status");
        }

        ticket.setStatus(to);
        Ticket saved = ticketRepository.save(ticket);

        createHistory(saved, requester, from, to, "STATUS_CHANGED", "Status changed");

        return TicketMapper.toResponse(saved);
    }

    @Override
    public TicketResponse assignTicket(UUID requesterId, UUID ticketId, UUID techId) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);

        if (requester.getRole() != UserRole.TECH && requester.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only TECH/ADMIN can assign tickets");
        }

        validateNotDone(ticket);

        User tech = getUserOrThrow(techId);
        if (tech.getRole() != UserRole.TECH && tech.getRole() != UserRole.ADMIN) {
            throw new BusinessRuleException("Assigned user must be TECH");
        }

        ticket.setAssignedTo(tech);
        Ticket saved = ticketRepository.save(ticket);

        createHistory(saved, requester, saved.getStatus(), saved.getStatus(), "ASSIGNED", "Ticket assigned");

        return TicketMapper.toResponse(saved);
    }

    @Override
    public void deleteTicket(UUID requesterId, UUID ticketId) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);


        if (requester.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can delete tickets");
        }

        ticketRepository.delete(ticket);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TicketHistoryResponse> getTicketHistory(UUID requesterId, UUID ticketId, Pageable pageable) {
        User requester = getUserOrThrow(requesterId);
        Ticket ticket = getTicketOrThrow(ticketId);

        validateReadAccess(requester, ticket);


        return ticketHistoryRepository
                .findByTicketIdOrderByCreatedAtDesc(ticket.getId(), pageable)
                .map(h -> new TicketHistoryResponse(
                        h.getId(),
                        h.getAction(),
                        h.getFromStatus(),
                        h.getToStatus(),
                        h.getNotes(),
                        TicketMapper.toUserSummary(h.getPerformedBy()),
                        h.getCreatedAt()
                ));
    }



    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Ticket getTicketOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));
    }

    private void validateNotDone(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.DONE) {
            throw new BusinessRuleException("Ticket is DONE and cannot be changed");
        }
    }

    private void validateReadAccess(User requester, Ticket ticket) {
        if (requester.getRole() == UserRole.CLIENT) {
            if (!ticket.getCreatedBy().getId().equals(requester.getId())) {
                throw new ForbiddenException("You are not allowed to view this ticket");
            }
        }
    }

    private void validateWriteAccess(User requester, Ticket ticket) {
        if (requester.getRole() == UserRole.CLIENT) {
            if (!ticket.getCreatedBy().getId().equals(requester.getId())) {
                throw new ForbiddenException("You are not allowed to update this ticket");
            }
        }
    }

    private void createHistory(Ticket ticket,
                               User performedBy,
                               TicketStatus from,
                               TicketStatus to,
                               String action,
                               String notes) {

        TicketHistory h = new TicketHistory();
        h.setTicket(ticket);
        h.setPerformedBy(performedBy);
        h.setFromStatus(from);
        h.setToStatus(to);
        h.setAction(action);
        h.setNotes(notes);

        ticketHistoryRepository.save(h);
    }
}
