package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;
import com.menditech.bank.customer.entity.*;
import com.menditech.bank.customer.enums.RoleCode;
import com.menditech.bank.customer.mapper.ClientMapper;
import com.menditech.bank.customer.messaging.producer.ClientEventPublisher;
import com.menditech.bank.customer.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountryPhoneCodeRepository countryPhoneCodeRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ClientEventPublisher clientEventPublisher;

    @InjectMocks
    private ClientService clientService;

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
                .clientCode("CLI2722163663")
                .password("1234")
                .roleCode("CLIENT")
                .isActive(true)
                .build();

        country = CountryEntity.builder()
                .id(1L)
                .name("Colombia")
                .iso2("CO")
                .iso3("COL")
                .numericCode("170")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        phoneCode = CountryPhoneCodeEntity.builder()
                .id(1L)
                .country(country)
                .phoneCode("+57")
                .label("Colombia")
                .isDefault(true)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        role = RoleEntity.builder()
                .id(1L)
                .code(RoleCode.CLIENT)
                .name("Cliente")
                .description("Cliente del banco")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        savedPerson = PersonEntity.builder()
                .id(1L)
                .identificationType(request.getIdentificationType())
                .identificationNumber(request.getIdentificationNumber())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .secondLastName(request.getSecondLastName())
                .fullName("Miguel Angel Mendigano Arismendy")
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .mobileNumber(request.getMobileNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .country(country)
                .countryPhoneCode(phoneCode)
                .city(request.getCity())
                .stateRegion(request.getStateRegion())
                .postalCode(request.getPostalCode())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        savedClient = ClientEntity.builder()
                .id(1L)
                .person(savedPerson)
                .role(role)
                .code("CLI2722163663")
                .passwordHash("$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2")
                .status(com.menditech.bank.customer.enums.ClientStatus.ACTIVE)
                .isActive(true)
                .failedLoginAttempts(0)
                .isLocked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        response = ClientResponse.builder()
                .clientId(1L)
                .personId(1L)
                .clientCode("CLI2722163663")
                .fullName("Miguel Angel Mendigano Arismendy")
                .email("miguel@test.com")
                .status("ACTIVE")
                .isActive(true)
                .build();
    }

    @Test
    void shouldCreateClientSuccessfully() {
        when(personRepository.existsByIdentificationNumber(request.getIdentificationNumber())).thenReturn(false);
        when(personRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(clientRepository.existsByCode(request.getClientCode())).thenReturn(false);
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryPhoneCodeRepository.findById(1L)).thenReturn(Optional.of(phoneCode));
        when(roleRepository.findByCode(RoleCode.CLIENT)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2");
        when(personRepository.save(any(PersonEntity.class))).thenReturn(savedPerson);
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(savedClient);
        when(clientMapper.toResponse(savedClient)).thenReturn(response);

        ClientResponse result = clientService.createClient(request);

        assertNotNull(result);
        assertEquals("CLI2722163663", result.getClientCode());
        assertEquals("ACTIVE", result.getStatus());
        assertTrue(result.getIsActive());

        verify(passwordEncoder).encode("1234");
        verify(personRepository).save(any(PersonEntity.class));
        verify(clientRepository).save(any(ClientEntity.class));
        verify(clientMapper).toResponse(savedClient);
        verify(clientEventPublisher).publishClientCreated(any());
    }
}
