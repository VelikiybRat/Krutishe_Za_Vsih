package org.example.krutishe_za_vsih.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Замовлення. Сторона "багато": належить одному аніматору й одній програмі.
 *
 * @NamedQuery - іменований JPQL-запит. Spring Data знаходить його за іменем
 * "OrderEntity.findByStatusAndPeriod" для методу репозиторію findByStatusAndPeriod.
 */
@Entity
@Table(name = "orders")
@NamedQuery(
        name = "OrderEntity.findByStatusAndPeriod",
        query = """
                SELECT o FROM OrderEntity o
                JOIN FETCH o.animator
                JOIN FETCH o.program
                WHERE o.status = :status
                  AND o.eventDate BETWEEN :from AND :to
                ORDER BY o.eventDate
                """
)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animator_id", nullable = false)
    private AnimatorEntity animator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private ProgramEntity program;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.NEW;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    protected OrderEntity() {
    }

    public OrderEntity(String clientName, LocalDate eventDate,
                       AnimatorEntity animator, ProgramEntity program,
                       BigDecimal price) {
        this.clientName = clientName;
        this.eventDate = eventDate;
        this.animator = animator;
        this.program = program;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public AnimatorEntity getAnimator() { return animator; }
    public void setAnimator(AnimatorEntity animator) { this.animator = animator; }
    public ProgramEntity getProgram() { return program; }
    public void setProgram(ProgramEntity program) { this.program = program; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
