package com.menditech.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_event_outbox", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEventOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ceo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cli_id", nullable = false)
    private ClientEntity client;

    @Column(name = "ceo_event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "ceo_payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "ceo_status", nullable = false, length = 20)
    private String status;

    @Column(name = "ceo_retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "ceo_next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "ceo_published_at")
    private LocalDateTime publishedAt;

    @Column(name = "ceo_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ceo_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}