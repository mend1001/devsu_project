package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.entity.ClientStatusHistoryEntity;
import com.menditech.bank.customer.entity.CountryEntity;
import com.menditech.bank.customer.entity.CountryPhoneCodeEntity;
import com.menditech.bank.customer.entity.PersonEntity;
import com.menditech.bank.customer.entity.RoleEntity;
import com.menditech.bank.customer.enums.ClientStatus;
import com.menditech.bank.customer.enums.RoleCode;
import com.menditech.bank.customer.exception.BusinessException;
import com.menditech.bank.customer.exception.ResourceNotFoundException;
import com.menditech.bank.customer.mapper.ClientMapper;
import com.menditech.bank.customer.messaging.producer.ClientEventPublisher;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.repository.ClientStatusHistoryRepository;
import com.menditech.bank.customer.repository.CountryPhoneCodeRepository;
import com.menditech.bank.customer.repository.CountryRepository;
import com.menditech.bank.customer.repository.PersonRepository;
import com.menditech.bank.customer.repository.RoleRepository;
import com.menditech.bank.customer.service.serviceImpl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private PersonRepository personRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private CountryRepository countryRepository;
    @Mock private CountryPhoneCodeRepository countryPhoneCodeRepository;
    @Mock private ClientStatusHistoryRepository clientStatusHistoryRepository;
    @Mock private ClientMapper clientMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ClientEventPublisher clientEventPublisher;

    @InjectMocks
    private ClientServiceImpl clientService;
    private ClientCreateRequest request;
    private CountryEntity country;
    private CountryPhoneCodeEntity phoneCode;
    private RoleEntity role;
    private PersonEntity savedPerson;
    private ClientEntity savedClient;
    private ClientResponse response;

    @BeforeEach
    void setUp() {
        request = ClientCreateRequest.builder()
                .identificationType("CC")
                .identificationNumber("123456789")
                .firstName("Miguel")
                .middleName("Angel")
                .lastName("Mendigano")
                .secondLastName("Arismendy")
                .gender("MALE")
                .email("miguel@test.com")
                .phoneNumber("6010000000")
                .mobileNumber("3000000000")
                .addressLine1("Calle 1")
                .addressLine2("Apto 101")
                .countryId(1L)
                .countryPhoneCodeId(1L)
                .city("Bogota")
                .stateRegion("Cundinamarca")
                .postalCode("110111")
                .password("1234")
                .roleCode("CLIENT")
                .isActive(true)
                .build();

        country = CountryEntity.builder()
                .id(1L).name("Colombia").iso2("CO").iso3("COL")
                .numericCode("170").isActive(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        phoneCode = CountryPhoneCodeEntity.builder()
                .id(1L).country(country).phoneCode("+57")
                .label("Colombia").isDefault(true).isActive(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        role = RoleEntity.builder()
                .id(1L).code(RoleCode.CLIENT).name("Cliente")
                .description("Cliente del banco").isActive(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        savedPerson = PersonEntity.builder()
                .id(1L)
                .identificationType("CC").identificationNumber("123456789")
                .firstName("Miguel").middleName("Angel")
                .lastName("Mendigano").secondLastName("Arismendy")
                .fullName("Miguel Angel Mendigano Arismendy")
                .gender("MALE").email("miguel@test.com")
                .phoneNumber("6010000000").mobileNumber("3000000000")
                .addressLine1("Calle 1").addressLine2("Apto 101")
                .country(country).countryPhoneCode(phoneCode)
                .city("Bogota").stateRegion("Cundinamarca").postalCode("110111")
                .isActive(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM").updatedBy("SYSTEM")
                .build();

        savedClient = ClientEntity.builder()
                .id(1L).person(savedPerson).role(role)
                .code("CLI0000000001")
                .passwordHash("$2a$10$hash")
                .status(ClientStatus.ACTIVE).isActive(true)
                .failedLoginAttempts(0).isLocked(false)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM").updatedBy("SYSTEM")
                .build();

        response = ClientResponse.builder()
                .clientId(1L).personId(1L)
                .clientCode("CLI0000000001")
                .fullName("Miguel Angel Mendigano Arismendy")
                .email("miguel@test.com")
                .status("ACTIVE").isActive(true)
                .build();
    }

    // -----------------------------------------------------------------------
    // createClient — happy path
    // -----------------------------------------------------------------------
    @Test
    void shouldCreateClientSuccessfully() {
        // validateCreateRequest(): primero cédula, luego email (ese es el orden real)
        when(personRepository.existsByIdentificationNumber("123456789")).thenReturn(false);
        when(personRepository.existsByEmail("miguel@test.com")).thenReturn(false);
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryPhoneCodeRepository.findById(1L)).thenReturn(Optional.of(phoneCode));
        when(roleRepository.findByCode(RoleCode.CLIENT)).thenReturn(Optional.of(role));
        when(clientRepository.getNextClientCodeSequence()).thenReturn(1L);
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$hash");
        when(personRepository.save(any(PersonEntity.class))).thenReturn(savedPerson);
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(savedClient);
        when(clientStatusHistoryRepository.save(any(ClientStatusHistoryEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(clientMapper.toResponse(savedClient)).thenReturn(response);

        ClientResponse result = clientService.createClient(request);

        assertNotNull(result);
        assertEquals("CLI0000000001", result.getClientCode());
        assertEquals("ACTIVE", result.getStatus());
        assertTrue(result.getIsActive());

        verify(passwordEncoder).encode("1234");
        verify(personRepository).save(any(PersonEntity.class));
        verify(clientRepository).save(any(ClientEntity.class));
        verify(clientStatusHistoryRepository).save(any(ClientStatusHistoryEntity.class));
        verify(clientMapper).toResponse(savedClient);
        verify(clientEventPublisher).publishClientCreated(any());
    }

    @Test
    void shouldThrowExceptionWhenIdentificationNumberAlreadyExists() {
        when(personRepository.existsByIdentificationNumber("123456789")).thenReturn(true);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("Identification number already exists", ex.getMessage());
        // La cédula falló primero → el email nunca se llega a consultar
        verify(personRepository, never()).existsByEmail(any());
        verify(clientRepository, never()).save(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // La cédula pasa → se llega al chequeo del email
        when(personRepository.existsByIdentificationNumber("123456789")).thenReturn(false);
        when(personRepository.existsByEmail("miguel@test.com")).thenReturn(true);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("Email already exists", ex.getMessage());
        verify(clientRepository, never()).save(any());
        verify(personRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // createClient — país no encontrado
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenCountryNotFound() {
        when(personRepository.existsByIdentificationNumber("123456789")).thenReturn(false);
        when(personRepository.existsByEmail("miguel@test.com")).thenReturn(false);
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("Country not found", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // createClient — rol inválido (no existe en RoleCode)
    // Mensaje exacto del código real: "Invalid role code"
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenRoleIsInvalid() {
        ClientCreateRequest badRequest = ClientCreateRequest.builder()
                .identificationType("CC")
                .identificationNumber("999888777")
                .firstName("Test").lastName("User")
                .gender("MALE").email("test.role@test.com")
                .addressLine1("Calle 1").countryId(1L)
                .password("1234").roleCode("UNKNOWN_ROLE")
                .isActive(true).build();

        when(personRepository.existsByIdentificationNumber("999888777")).thenReturn(false);
        when(personRepository.existsByEmail("test.role@test.com")).thenReturn(false);
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> clientService.createClient(badRequest)
        );

        assertEquals("Invalid role code", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // getClientById — cliente encontrado
    // -----------------------------------------------------------------------
    @Test
    void shouldReturnClientById() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(savedClient));
        when(clientMapper.toResponse(savedClient)).thenReturn(response);

        ClientResponse result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals("CLI0000000001", result.getClientCode());
        verify(clientRepository).findById(1L);
    }

    // -----------------------------------------------------------------------
    // getClientById — cliente no encontrado
    // Mensaje exacto del código real: "Client not found"
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.getClientById(999L)
        );

        assertEquals("Client not found", ex.getMessage());
    }

    @Test
    void shouldDeactivateClientSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(savedClient));
        when(personRepository.save(any(PersonEntity.class))).thenReturn(savedPerson);
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(savedClient);
        when(clientStatusHistoryRepository.save(any(ClientStatusHistoryEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        clientService.deleteClient(1L);

        assertFalse(savedClient.getIsActive());
        assertEquals(ClientStatus.INACTIVE, savedClient.getStatus());
        verify(clientRepository).save(savedClient);
        verify(personRepository).save(savedPerson);
        verify(clientStatusHistoryRepository).save(any(ClientStatusHistoryEntity.class));
        verify(clientEventPublisher).publishClientUpdated(any());
    }
}
