package viniciusDias1001.com.github.gerenciamento_de_tickets.service.impl;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.*;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {
    @Mock TicketRepository ticketRepository;
    @Mock TicketHistoryRepository historyRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    TicketServiceImpl service;

    private User admin;
    private User tech;
    private User client;
    private Ticket ticket;

    @BeforeEach
    void setup() {
        admin = user("Admin", "admin@local.com", UserRole.ADMIN);
        tech = user("Tech", "tech@local.com", UserRole.TECH);
        client = user("Client", "client@local.com", UserRole.CLIENT);

        ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setTitle("Erro");
        ticket.setDescription("desc");
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(client);
        ticket.setAssignedTo(null);
        ticket.setCreatedAt(OffsetDateTime.now());
        ticket.setUpdatedAt(OffsetDateTime.now());
    }


    @Test
    void updateTicket_whenDone_shouldThrowBusinessRule() {
        ticket.setStatus(TicketStatus.DONE);

        when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        UpdateTicketRequest req = new UpdateTicketRequest("novo", "nova desc", TicketPriority.LOW);

        assertThrows(BusinessRuleException.class,
                () -> service.updateTicket(client.getId(), ticket.getId(), req));

        verify(ticketRepository, never()).save(any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    void changeStatus_whenDone_shouldThrowBusinessRule() {
        ticket.setStatus(TicketStatus.DONE);

        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        ChangeStatusRequest req = new ChangeStatusRequest(TicketStatus.IN_PROGRESS);

        assertThrows(BusinessRuleException.class,
                () -> service.changeStatus(admin.getId(), ticket.getId(), req));

        verify(ticketRepository, never()).save(any());
        verify(historyRepository, never()).save(any());
    }


    @Test
    void getTicketById_clientAccessingOthersTicket_shouldThrowForbidden() {
        User otherClient = user("Other", "o@local.com", UserRole.CLIENT);
        ticket.setCreatedBy(otherClient);

        when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        assertThrows(ForbiddenException.class,
                () -> service.getTicketById(client.getId(), ticket.getId()));
    }


    @Test
    void changeStatus_shouldSaveHistory() {
        ticket.setAssignedTo(tech); // se sua regra exigir assigned pra tech

        when(userRepository.findById(tech.getId())).thenReturn(Optional.of(tech));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        ChangeStatusRequest req = new ChangeStatusRequest(TicketStatus.IN_PROGRESS);

        service.changeStatus(tech.getId(), ticket.getId(), req);

        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());

        verify(historyRepository, times(1)).save(argThat(h ->
                h.getTicket().getId().equals(ticket.getId())
                        && h.getPerformedBy().getId().equals(tech.getId())
                        && h.getFromStatus() == TicketStatus.OPEN
                        && h.getToStatus() == TicketStatus.IN_PROGRESS
        ));
    }


    @Test
    void assignTicket_shouldSaveHistoryAndSetAssignedTo() {
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(userRepository.findById(tech.getId())).thenReturn(Optional.of(tech));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        service.assignTicket(admin.getId(), ticket.getId(), tech.getId());

        assertNotNull(ticket.getAssignedTo());
        assertEquals(tech.getId(), ticket.getAssignedTo().getId());

        verify(historyRepository, times(1)).save(any(TicketHistory.class));
    }

    @Test
    void createTicket_whenRequesterIsTech_shouldThrowForbidden() {
        when(userRepository.findById(tech.getId())).thenReturn(Optional.of(tech));

        CreateTicketRequest req = new CreateTicketRequest("t", "d", TicketPriority.MEDIUM);

        assertThrows(ForbiddenException.class,
                () -> service.createTicket(tech.getId(), req));

        verify(ticketRepository, never()).save(any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    void createTicket_whenClient_shouldSaveTicketAndHistory() {
        when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        CreateTicketRequest req = new CreateTicketRequest("t", "d", TicketPriority.HIGH);

        TicketResponse res = service.createTicket(client.getId(), req);

        assertNotNull(res.id());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(historyRepository, times(1)).save(argThat(h ->
                "CREATED".equals(h.getAction()) &&
                        h.getToStatus() == TicketStatus.OPEN
        ));
    }

    @Test
    void changeStatus_whenSameStatus_shouldThrowBusinessRule() {
        ticket.setStatus(TicketStatus.OPEN);

        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        ChangeStatusRequest req = new ChangeStatusRequest(TicketStatus.OPEN);

        assertThrows(BusinessRuleException.class,
                () -> service.changeStatus(admin.getId(), ticket.getId(), req));

        verify(ticketRepository, never()).save(any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    void getTicketHistory_clientFromAnotherTicket_shouldThrowForbidden() {
        User other = user("Other", "o@x.com", UserRole.CLIENT);
        ticket.setCreatedBy(other);

        when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        assertThrows(ForbiddenException.class,
                () -> service.getTicketHistory(client.getId(), ticket.getId(), Pageable.ofSize(10)));

        verify(historyRepository, never()).findByTicketIdOrderByCreatedAtDesc(any(), any());
    }



    private User user(String name, String email, UserRole role) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setName(name);
        u.setEmail(email);
        u.setRole(role);
        u.setPasswordHash("x");
        u.setCreatedAt(OffsetDateTime.now());
        u.setUpdatedAt(OffsetDateTime.now());
        return u;
    }
}
