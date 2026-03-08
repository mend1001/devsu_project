package com.menditech.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_status_history", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "csh_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cli_id", nullable = false)
    private ClientEntity client;

    @Column(name = "csh_old_status", length = 20)
    private String oldStatus;

    @Column(name = "csh_new_status", nullable = false, length = 20)
    private String newStatus;

    @Column(name = "csh_reason", length = 255)
    private String reason;

    @Column(name = "csh_changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "csh_changed_by", length = 60)
    private String changedBy;
}
