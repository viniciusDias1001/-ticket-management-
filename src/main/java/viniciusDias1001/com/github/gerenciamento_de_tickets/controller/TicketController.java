package viniciusDias1001.com.github.gerenciamento_de_tickets.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.service.TicketHistoryService;
import viniciusDias1001.com.github.gerenciamento_de_tickets.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketHistoryService ticketHistoryService;

    public TicketController(TicketService ticketService, TicketHistoryService ticketHistoryService) {
        this.ticketService = ticketService;
        this.ticketHistoryService = ticketHistoryService;
    }

    private UUID requesterId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@AuthenticationPrincipal Jwt jwt,
                                 @RequestBody @Valid CreateTicketRequest request) {
        return ticketService.createTicket(requesterId(jwt), request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse getById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return ticketService.getTicketById(requesterId(jwt), id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TicketResponse> list(@AuthenticationPrincipal Jwt jwt,
                                     @RequestParam(required = false) TicketStatus status,
                                     @RequestParam(required = false) TicketPriority priority,
                                     Pageable pageable) {
        return ticketService.listTickets(requesterId(jwt), status, priority, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TicketResponse update(@AuthenticationPrincipal Jwt jwt,
                                 @PathVariable UUID id,
                                 @RequestBody @Valid UpdateTicketRequest request) {
        return ticketService.updateTicket(requesterId(jwt), id, request);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TicketResponse changeStatus(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable UUID id,
                                       @RequestBody @Valid ChangeStatusRequest request) {
        return ticketService.changeStatus(requesterId(jwt), id, request);
    }

    @PatchMapping("/{id}/assign/{techId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TicketResponse assign(@AuthenticationPrincipal Jwt jwt,
                                 @PathVariable UUID id,
                                 @PathVariable UUID techId) {
        return ticketService.assignTicket(requesterId(jwt), id, techId);
    }

    //  HISTORY AS SUBQUERY
    @GetMapping("/{id}/history")
    @ResponseStatus(HttpStatus.OK)
    public Page<TicketHistoryResponse> history(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable UUID id,
                                               Pageable pageable) {
        return ticketHistoryService.listByTicket(requesterId(jwt), id, pageable);
    }
}
