package com.menditech.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "person", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id")
    private Long id;

    @Column(name = "per_identification_type", nullable = false, length = 20)
    private String identificationType;

    @Column(name = "per_identification_number", nullable = false, length = 30, unique = true)
    private String identificationNumber;

    @Column(name = "per_first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "per_middle_name", length = 60)
    private String middleName;

    @Column(name = "per_last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "per_second_last_name", length = 60)
    private String secondLastName;

    @Column(name = "per_full_name", nullable = false, length = 180)
    private String fullName;

    @Column(name = "per_gender", nullable = false, length = 20)
    private String gender;

    @Column(name = "per_birth_date")
    private LocalDate birthDate;

    @Column(name = "per_age")
    private Integer age;

    @Column(name = "per_email", length = 120, unique = true)
    private String email;

    @Column(name = "per_phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "per_mobile_number", length = 30)
    private String mobileNumber;

    @Column(name = "per_address_line_1", nullable = false, length = 150)
    private String addressLine1;

    @Column(name = "per_address_line_2", length = 150)
    private String addressLine2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cou_id", nullable = false)
    private CountryEntity country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpc_id")
    private CountryPhoneCodeEntity countryPhoneCode;

    @Column(name = "per_city", length = 80)
    private String city;

    @Column(name = "per_state_region", length = 80)
    private String stateRegion;

    @Column(name = "per_postal_code", length = 20)
    private String postalCode;

    @Column(name = "per_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "per_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "per_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "per_created_by", length = 60)
    private String createdBy;

    @Column(name = "per_updated_by", length = 60)
    private String updatedBy;
}
