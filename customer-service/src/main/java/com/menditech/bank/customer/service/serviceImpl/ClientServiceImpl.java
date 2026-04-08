package com.menditech.bank.customer.service.serviceImpl;

import com.menditech.bank.customer.dto.request.ChangePasswordRequest;
import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.request.ClientUpdateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;

import com.menditech.bank.customer.enums.ClientStatus;
import com.menditech.bank.customer.enums.RoleCode;
import com.menditech.bank.customer.exception.BusinessException;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.exception.ResourceNotFoundException;
import com.menditech.bank.customer.mapper.ClientMapper;
import com.menditech.bank.customer.messaging.event.ClientCreatedEvent;
import com.menditech.bank.customer.messaging.event.ClientUpdatedEvent;
import com.menditech.bank.customer.messaging.producer.ClientEventPublisher;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.entity.ClientStatusHistoryEntity;
import com.menditech.bank.customer.entity.CountryEntity;
import com.menditech.bank.customer.entity.CountryPhoneCodeEntity;
import com.menditech.bank.customer.entity.PersonEntity;
import com.menditech.bank.customer.entity.RoleEntity;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.repository.ClientStatusHistoryRepository;
import com.menditech.bank.customer.repository.CountryPhoneCodeRepository;
import com.menditech.bank.customer.repository.CountryRepository;
import com.menditech.bank.customer.repository.PersonRepository;
import com.menditech.bank.customer.repository.RoleRepository;

import com.menditech.bank.customer.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CLIENT_CODE_PREFIX = "CLI";
    private static final String SYSTEM_USER = "SYSTEM";

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final CountryPhoneCodeRepository countryPhoneCodeRepository;
    private final ClientStatusHistoryRepository clientStatusHistoryRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final ClientEventPublisher clientEventPublisher;

    @Override
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
        LocalDateTime now = LocalDateTime.now();
        String actor = getCurrentUser();

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
                .createdBy(actor)
                .updatedBy(actor)
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
                .createdBy(actor)
                .updatedBy(actor)
                .build();

        ClientEntity savedClient = clientRepository.save(client);

        saveStatusHistory(savedClient, null, savedClient.getStatus(), "Client created", actor);
        publishClientCreated(savedClient);

        return clientMapper.toResponse(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long clientId) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> getAllClients(Pageable pageable) {
        log.debug("Fetching clients with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return clientRepository.findAll(pageable)
                .map(clientMapper::toResponse);
    }

    @Override
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

        ClientStatus oldStatus = client.getStatus();
        ClientStatus newStatus = request.getIsActive() ? ClientStatus.ACTIVE : ClientStatus.INACTIVE;
        LocalDateTime now = LocalDateTime.now();
        String actor = getCurrentUser();

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
        person.setUpdatedAt(now);
        person.setUpdatedBy(actor);

        client.setIsActive(request.getIsActive());
        client.setStatus(newStatus);
        client.setUpdatedAt(now);
        client.setUpdatedBy(actor);

        personRepository.save(person);
        ClientEntity updatedClient = clientRepository.save(client);

        if (oldStatus != newStatus) {
            saveStatusHistory(updatedClient, oldStatus, newStatus, "Client updated", actor);
        }

        publishClientUpdated(updatedClient);

        return clientMapper.toResponse(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long clientId) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        PersonEntity person = client.getPerson();
        ClientStatus oldStatus = client.getStatus();
        LocalDateTime now = LocalDateTime.now();
        String actor = getCurrentUser();

        person.setIsActive(false);
        person.setUpdatedAt(now);
        person.setUpdatedBy(actor);

        client.setIsActive(false);
        client.setStatus(ClientStatus.INACTIVE);
        client.setUpdatedAt(now);
        client.setUpdatedBy(actor);

        personRepository.save(person);
        ClientEntity updatedClient = clientRepository.save(client);

        saveStatusHistory(updatedClient, oldStatus, ClientStatus.INACTIVE, "Client deactivated", actor);
        publishClientUpdated(updatedClient);
    }

    @Override
    @Transactional
    public void changePassword(Long clientId, ChangePasswordRequest request) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), client.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), client.getPasswordHash())) {
            throw new BusinessException("New password must be different from the current password");
        }

        client.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        client.setUpdatedAt(LocalDateTime.now());
        client.setUpdatedBy(getCurrentUser());

        clientRepository.save(client);
        log.info("Password changed successfully for clientId: {}", clientId);
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
        return CLIENT_CODE_PREFIX + formattedNumber;
    }

    private void saveStatusHistory(ClientEntity client, ClientStatus oldStatus, ClientStatus newStatus, String reason, String actor) {
        ClientStatusHistoryEntity history = ClientStatusHistoryEntity.builder()
                .client(client)
                .oldStatus(oldStatus != null ? oldStatus.name() : null)
                .newStatus(newStatus.name())
                .reason(reason)
                .changedAt(LocalDateTime.now())
                .changedBy(actor)
                .build();

        clientStatusHistoryRepository.save(history);
    }

    private void publishClientCreated(ClientEntity client) {
        PersonEntity person = client.getPerson();
        RoleEntity role = client.getRole();

        clientEventPublisher.publishClientCreated(
                ClientCreatedEvent.builder()
                        .clientId(client.getId())
                        .personId(person.getId())
                        .roleId(role.getId())
                        .clientCode(client.getCode())
                        .fullName(person.getFullName())
                        .identificationNumber(person.getIdentificationNumber())
                        .email(person.getEmail())
                        .phoneNumber(person.getMobileNumber() != null ? person.getMobileNumber() : person.getPhoneNumber())
                        .status(client.getStatus().name())
                        .isActive(client.getIsActive())
                        .eventDate(LocalDateTime.now())
                        .build()
        );
    }

    private void publishClientUpdated(ClientEntity client) {
        PersonEntity person = client.getPerson();
        RoleEntity role = client.getRole();

        clientEventPublisher.publishClientUpdated(
                ClientUpdatedEvent.builder()
                        .clientId(client.getId())
                        .personId(person.getId())
                        .roleId(role.getId())
                        .clientCode(client.getCode())
                        .fullName(person.getFullName())
                        .identificationNumber(person.getIdentificationNumber())
                        .email(person.getEmail())
                        .phoneNumber(person.getMobileNumber() != null ? person.getMobileNumber() : person.getPhoneNumber())
                        .status(client.getStatus().name())
                        .isActive(client.getIsActive())
                        .eventDate(LocalDateTime.now())
                        .build()
        );
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getName() != null) ? auth.getName() : SYSTEM_USER;
    }
}