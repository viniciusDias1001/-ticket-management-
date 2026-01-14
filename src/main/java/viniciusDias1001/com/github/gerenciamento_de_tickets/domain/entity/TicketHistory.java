package viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_history",
        indexes = {
                @Index(name = "ix_history_ticket", columnList = "ticket_id"),
                @Index(name = "ix_history_created_at", columnList = "createdAt")
        }
)
@Getter
@Setter
public class TicketHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ticket_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_history_ticket")
    )
    private Ticket ticket;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "performed_by_id",
            foreignKey = @ForeignKey(name = "fk_history_performed_by")
    )
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private TicketStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", length = 20)
    private TicketStatus toStatus;

    @Column(nullable = false, length = 255)
    private String action; // ex: "STATUS_CHANGED", "ASSIGNED", "UPDATED"

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }

}
