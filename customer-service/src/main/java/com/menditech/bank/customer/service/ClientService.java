package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.request.ClientUpdateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;
import com.menditech.bank.customer.entity.*;
import com.menditech.bank.customer.enums.ClientStatus;
import com.menditech.bank.customer.enums.RoleCode;
import com.menditech.bank.customer.exception.BusinessException;
import com.menditech.bank.customer.exception.ResourceNotFoundException;
import com.menditech.bank.customer.mapper.ClientMapper;
import com.menditech.bank.customer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.menditech.bank.customer.messaging.event.ClientCreatedEvent;
import com.menditech.bank.customer.messaging.producer.ClientEventPublisher;
import java.security.SecureRandom;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final CountryPhoneCodeRepository countryPhoneCodeRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final ClientEventPublisher clientEventPublisher;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CLIENT_CODE_PREFIX = "CLI";
    private static final int CLIENT_CODE_DIGITS = 10;
    private static final int MAX_CLIENT_CODE_ATTEMPTS = 20;

    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        validateCreateRequest(request);

        CountryEntity country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found"));

        CountryPhoneCodeEntity countryPhoneCode = null;
        if (request.getCountryPhoneCodeId() != null) {
            countryPhoneCode = countryPhoneCodeRepository.findById(request.getCountryPhoneCodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Country phone code not found"));
        }

        RoleCode roleCode;
        try {
            roleCode = RoleCode.valueOf(request.getRoleCode().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid role code");
        }

        RoleEntity role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        String generatedClientCode = generateClientCode();

        if (clientRepository.existsByCode(generatedClientCode)) {
            throw new BusinessException("Client code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        PersonEntity person = PersonEntity.builder()
                .identificationType(request.getIdentificationType())
                .identificationNumber(request.getIdentificationNumber())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .secondLastName(request.getSecondLastName())
                .fullName(buildFullName(
                        request.getFirstName(),
                        request.getMiddleName(),
                        request.getLastName(),
                        request.getSecondLastName()
                ))
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .mobileNumber(request.getMobileNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .country(country)
                .countryPhoneCode(countryPhoneCode)
                .city(request.getCity())
                .stateRegion(request.getStateRegion())
                .postalCode(request.getPostalCode())
                .isActive(request.getIsActive())
                .createdAt(now)
                .updatedAt(now)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        PersonEntity savedPerson = personRepository.save(person);

        ClientEntity client = ClientEntity.builder()
                .person(savedPerson)
                .role(role)
                .code(generatedClientCode)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .passwordSalt(null)
                .status(request.getIsActive() ? ClientStatus.ACTIVE : ClientStatus.INACTIVE)
                .isActive(request.getIsActive())
                .failedLoginAttempts(0)
                .isLocked(false)
                .createdAt(now)
                .updatedAt(now)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        ClientEntity savedClient = clientRepository.save(client);

        clientEventPublisher.publishClientCreated(
                ClientCreatedEvent.builder()
                        .clientId(savedClient.getId())
                        .personId(savedPerson.getId())
                        .roleId(role.getId())
                        .clientCode(savedClient.getCode())
                        .fullName(savedPerson.getFullName())
                        .identificationNumber(savedPerson.getIdentificationNumber())
                        .email(savedPerson.getEmail())
                        .phoneNumber(savedPerson.getMobileNumber() != null
                                ? savedPerson.getMobileNumber()
                                : savedPerson.getPhoneNumber())
                        .status(savedClient.getStatus().name())
                        .isActive(savedClient.getIsActive())
                        .eventDate(LocalDateTime.now())
                        .build()
        );

        return clientMapper.toResponse(savedClient);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long clientId) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return clientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Transactional
    public ClientResponse updateClient(Long clientId, ClientUpdateRequest request) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        PersonEntity person = client.getPerson();

        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(person.getEmail())
                && personRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        CountryEntity country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found"));

        CountryPhoneCodeEntity countryPhoneCode = null;
        if (request.getCountryPhoneCodeId() != null) {
            countryPhoneCode = countryPhoneCodeRepository.findById(request.getCountryPhoneCodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Country phone code not found"));
        }

        person.setFirstName(request.getFirstName());
        person.setMiddleName(request.getMiddleName());
        person.setLastName(request.getLastName());
        person.setSecondLastName(request.getSecondLastName());
        person.setFullName(buildFullName(
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName(),
                request.getSecondLastName()
        ));
        person.setGender(request.getGender());
        person.setEmail(request.getEmail());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setMobileNumber(request.getMobileNumber());
        person.setAddressLine1(request.getAddressLine1());
        person.setAddressLine2(request.getAddressLine2());
        person.setCountry(country);
        person.setCountryPhoneCode(countryPhoneCode);
        person.setCity(request.getCity());
        person.setStateRegion(request.getStateRegion());
        person.setPostalCode(request.getPostalCode());
        person.setIsActive(request.getIsActive());
        person.setUpdatedAt(LocalDateTime.now());
        person.setUpdatedBy("SYSTEM");

        client.setIsActive(request.getIsActive());
        client.setStatus(request.getIsActive() ? ClientStatus.ACTIVE : ClientStatus.INACTIVE);
        client.setUpdatedAt(LocalDateTime.now());
        client.setUpdatedBy("SYSTEM");

        personRepository.save(person);
        ClientEntity updatedClient = clientRepository.save(client);

        return clientMapper.toResponse(updatedClient);
    }

    private void validateCreateRequest(ClientCreateRequest request) {
        if (personRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw new BusinessException("Identification number already exists");
        }

        if (request.getEmail() != null && personRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
    }

    private String buildFullName(String firstName, String middleName, String lastName, String secondLastName) {
        return String.join(" ",
                firstName != null ? firstName.trim() : "",
                middleName != null ? middleName.trim() : "",
                lastName != null ? lastName.trim() : "",
                secondLastName != null ? secondLastName.trim() : ""
        ).trim().replaceAll("\\s+", " ");
    }
    private String generateClientCode() {

        Long sequenceValue = clientRepository.getNextClientCodeSequence();

        String formattedNumber = String.format("%010d", sequenceValue);

        return "CLI" + formattedNumber;
    }
}