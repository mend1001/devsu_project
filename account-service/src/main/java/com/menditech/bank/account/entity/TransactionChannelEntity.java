package com.menditech.bank.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_channel", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tch_id")
    private Long id;

    @Column(name = "tch_code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "tch_name", nullable = false, length = 50)
    private String name;

    @Column(name = "tch_description", length = 255)
    private String description;

    @Column(name = "tch_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "tch_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "tch_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
