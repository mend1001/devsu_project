package com.menditech.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "country", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cou_id")
    private Long id;

    @Column(name = "cou_name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "cou_iso2", nullable = false, length = 2, unique = true)
    private String iso2;

    @Column(name = "cou_iso3", nullable = false, length = 3, unique = true)
    private String iso3;

    @Column(name = "cou_numeric_code", length = 3)
    private String numericCode;

    @Column(name = "cou_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "cou_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cou_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}