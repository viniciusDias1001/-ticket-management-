package viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets",
        indexes = {
                @Index(name = "ix_ticket_status", columnList = "status"),
                @Index(name = "ix_ticket_priority", columnList = "priority"),
                @Index(name = "ix_ticket_created_by", columnList = "created_by_id"),
                @Index(name = "ix_ticket_assigned_to", columnList = "assigned_to_id")
        }
)
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 140)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority = TicketPriority.MEDIUM;

    // who open the ticket
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "created_by_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ticket_created_by")
    )
    private User createdBy;

    // tech responsibility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assigned_to_id",
            foreignKey = @ForeignKey(name = "fk_ticket_assigned_to")
    )
    private User assignedTo;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;


    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketHistory> history = new ArrayList<>();

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }


    public void addHistory(TicketHistory h) {
        h.setTicket(this);
        this.history.add(h);
    }
}
