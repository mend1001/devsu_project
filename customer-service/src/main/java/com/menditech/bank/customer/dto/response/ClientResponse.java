package com.menditech.bank.customer.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long clientId;
    private Long personId;
    private String clientCode;
    private String roleCode;
    private String roleName;
    private String status;
    private Boolean isActive;
    private Boolean isLocked;

    private String identificationType;
    private String identificationNumber;

    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String fullName;
    private String gender;

    private String email;
    private String phoneNumber;
    private String mobileNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateRegion;
    private String postalCode;

    private Long countryId;
    private String countryName;
    private String countryIso2;

    private Long countryPhoneCodeId;
    private String phoneCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}