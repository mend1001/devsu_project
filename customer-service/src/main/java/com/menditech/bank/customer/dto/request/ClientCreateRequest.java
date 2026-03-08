package com.menditech.bank.customer.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateRequest {

    @NotBlank(message = "Identification type is required")
    @Size(max = 20, message = "Identification type must not exceed 20 characters")
    private String identificationType;

    @NotBlank(message = "Identification number is required")
    @Size(max = 30, message = "Identification number must not exceed 30 characters")
    private String identificationNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 60, message = "First name must not exceed 60 characters")
    private String firstName;

    @Size(max = 60, message = "Middle name must not exceed 60 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 60, message = "Last name must not exceed 60 characters")
    private String lastName;

    @Size(max = 60, message = "Second last name must not exceed 60 characters")
    private String secondLastName;

    @NotBlank(message = "Gender is required")
    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    @Email(message = "Email format is invalid")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phoneNumber;

    @Size(max = 30, message = "Mobile number must not exceed 30 characters")
    private String mobileNumber;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 150, message = "Address line 1 must not exceed 150 characters")
    private String addressLine1;

    @Size(max = 150, message = "Address line 2 must not exceed 150 characters")
    private String addressLine2;

    @NotNull(message = "Country id is required")
    private Long countryId;

    private Long countryPhoneCodeId;

    @Size(max = 80, message = "City must not exceed 80 characters")
    private String city;

    @Size(max = 80, message = "State/Region must not exceed 80 characters")
    private String stateRegion;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @NotBlank(message = "Client code is required")
    @Size(max = 30, message = "Client code must not exceed 30 characters")
    private String clientCode;

    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;

    @NotBlank(message = "Role code is required")
    @Size(max = 30, message = "Role code must not exceed 30 characters")
    private String roleCode;

    @NotNull(message = "Is active flag is required")
    private Boolean isActive;
}
