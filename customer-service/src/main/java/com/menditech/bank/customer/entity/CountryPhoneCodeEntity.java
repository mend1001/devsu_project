package com.menditech.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "country_phone_code", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryPhoneCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cpc_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cou_id", nullable = false)
    private CountryEntity country;

    @Column(name = "cpc_phone_code", nullable = false, length = 10)
    private String phoneCode;

    @Column(name = "cpc_label", length = 50)
    private String label;

    @Column(name = "cpc_is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "cpc_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "cpc_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cpc_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
