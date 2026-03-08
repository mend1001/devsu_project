package com.menditech.bank.customer.mapper;

import com.menditech.bank.customer.dto.response.ClientResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.entity.PersonEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponse toResponse(ClientEntity client) {
        PersonEntity person = client.getPerson();

        return ClientResponse.builder()
                .clientId(client.getId())
                .personId(person.getId())
                .clientCode(client.getCode())
                .roleCode(client.getRole() != null && client.getRole().getCode() != null
                        ? client.getRole().getCode().name()
                        : null)
                .roleName(client.getRole() != null ? client.getRole().getName() : null)
                .status(client.getStatus() != null ? client.getStatus().name() : null)
                .isActive(client.getIsActive())
                .isLocked(client.getIsLocked())
                .identificationType(person.getIdentificationType())
                .identificationNumber(person.getIdentificationNumber())
                .firstName(person.getFirstName())
                .middleName(person.getMiddleName())
                .lastName(person.getLastName())
                .secondLastName(person.getSecondLastName())
                .fullName(person.getFullName())
                .gender(person.getGender())
                .email(person.getEmail())
                .phoneNumber(person.getPhoneNumber())
                .mobileNumber(person.getMobileNumber())
                .addressLine1(person.getAddressLine1())
                .addressLine2(person.getAddressLine2())
                .city(person.getCity())
                .stateRegion(person.getStateRegion())
                .postalCode(person.getPostalCode())
                .countryId(person.getCountry() != null ? person.getCountry().getId() : null)
                .countryName(person.getCountry() != null ? person.getCountry().getName() : null)
                .countryIso2(person.getCountry() != null ? person.getCountry().getIso2() : null)
                .countryPhoneCodeId(person.getCountryPhoneCode() != null ? person.getCountryPhoneCode().getId() : null)
                .phoneCode(person.getCountryPhoneCode() != null ? person.getCountryPhoneCode().getPhoneCode() : null)
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}