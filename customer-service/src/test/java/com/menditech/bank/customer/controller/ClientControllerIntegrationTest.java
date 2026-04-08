package com.menditech.bank.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.menditech.bank.customer.messaging.producer.ClientEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Import(ClientControllerIntegrationTest.TestConfig.class)
class ClientControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientEventPublisher clientEventPublisher;

    @BeforeEach
    void setUp() {
        doNothing().when(clientEventPublisher).publishClientCreated(any());
        doNothing().when(clientEventPublisher).publishClientUpdated(any());
    }

    private String generateRandomSuffix() {
        return String.valueOf((long)(Math.random() * 900000000L) + 100000000L);
    }

    private String buildCreateRequest(String identificationNumber, String email) {
        return String.format("""
                {
                  "identificationType": "CC",
                  "identificationNumber": "%s",
                  "firstName": "Miguel",
                  "middleName": "Angel",
                  "lastName": "Mendigano",
                  "secondLastName": "Arismendy",
                  "gender": "MALE",
                  "email": "%s",
                  "phoneNumber": "6010000000",
                  "mobileNumber": "3000000000",
                  "addressLine1": "Calle 123 #45-67",
                  "addressLine2": "Apto 101",
                  "countryId": 1,
                  "countryPhoneCodeId": 1,
                  "city": "Bogota",
                  "stateRegion": "Cundinamarca",
                  "postalCode": "110111",
                  "password": "1234",
                  "roleCode": "CLIENT",
                  "isActive": true
                }
                """, identificationNumber, email);
    }

    @Test
    void shouldCreateClientSuccessfully() throws Exception {
        String randomSuffix = generateRandomSuffix();
        String identificationNumber = "98762" + randomSuffix;
        String email = "integration." + randomSuffix + "@test.com";
        String requestBody = buildCreateRequest(identificationNumber, email);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Client created successfully"))
                .andExpect(jsonPath("$.data.clientId").exists())
                .andExpect(jsonPath("$.data.clientCode").isNotEmpty())
                .andExpect(jsonPath("$.data.clientCode").value(containsString("CLI")))
                .andExpect(jsonPath("$.data.firstName").value("Miguel"))
                .andExpect(jsonPath("$.data.lastName").value("Mendigano"))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.identificationNumber").value(identificationNumber))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    @Test
    void shouldGetAllClientsWithPaginationSuccessfully() throws Exception {
        mockMvc.perform(get("/api/clients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Clients retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(greaterThan(0)))
                .andExpect(jsonPath("$.data.totalPages").exists())
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.content[0].clientId").exists())
                .andExpect(jsonPath("$.data.content[0].clientCode").exists())
                .andExpect(jsonPath("$.data.content[0].firstName").exists())
                .andExpect(jsonPath("$.data.content[0].email").exists());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingClientWithDuplicateEmail() throws Exception {
        // First client creation
        String randomSuffix = generateRandomSuffix();
        String identificationNumber1 = "11111" + randomSuffix;
        String email = "duplicate." + randomSuffix + "@test.com";

        String requestBody1 = buildCreateRequest(identificationNumber1, email);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isCreated());

        // Second client with same email
        String identificationNumber2 = "22222" + randomSuffix;
        String requestBody2 = buildCreateRequest(identificationNumber2, email);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Email already exists")));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingClientWithInvalidData() throws Exception {
        String invalidRequest = """
                {
                  "identificationType": "CC",
                  "identificationNumber": "123456",
                  "firstName": "",
                  "lastName": "Test",
                  "email": "invalid-email-format",
                  "countryId": 1,
                  "password": "1234",
                  "roleCode": "CLIENT",
                  "isActive": true
                }
                """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentClient() throws Exception {
        mockMvc.perform(get("/api/clients/{clientId}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(containsString("Client not found")));
    }
}