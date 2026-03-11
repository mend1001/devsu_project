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

    @Test
    void shouldCreateClientSuccessfully() throws Exception {
        String randomSuffix = String.valueOf((long)(Math.random() * 900000000L) + 100000000L);
        String identificationNumber = "98762" + randomSuffix;
        String email = "integration." + randomSuffix + "@test.com";
        String requestBody = """
                {
                  "identificationType": "CC",
                  "identificationNumber": "9876299910",
                  "firstName": "Miguel",
                  "middleName": "Angel",
                  "lastName": "Mendigano",
                  "secondLastName": "Arismendy",
                  "gender": "MALE",
                  "email": "integr11991.client@test.com",
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
                """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                ;
    }

    @Test
    void shouldGetAllClientsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Clients retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data[0].clientId").exists())
                .andExpect(jsonPath("$.data[0].personId").exists())
                .andExpect(jsonPath("$.data[0].clientCode").exists())
                .andExpect(jsonPath("$.data[0].roleCode").value("CLIENT"))
                .andExpect(jsonPath("$.data[0].roleName").value("Cliente"))
                .andExpect(jsonPath("$.data[0].status").exists())
                .andExpect(jsonPath("$.data[0].isActive").isBoolean())
                .andExpect(jsonPath("$.data[0].isLocked").isBoolean())
                .andExpect(jsonPath("$.data[0].identificationType").exists())
                .andExpect(jsonPath("$.data[0].identificationNumber").exists())
                .andExpect(jsonPath("$.data[0].firstName").exists())
                .andExpect(jsonPath("$.data[0].lastName").exists())
                .andExpect(jsonPath("$.data[0].fullName").exists())
                .andExpect(jsonPath("$.data[0].gender").exists())
                .andExpect(jsonPath("$.data[0].email").exists())
                .andExpect(jsonPath("$.data[0].phoneNumber").exists())
                .andExpect(jsonPath("$.data[0].mobileNumber").exists())
                .andExpect(jsonPath("$.data[0].addressLine1").exists())
                .andExpect(jsonPath("$.data[0].city").exists())
                .andExpect(jsonPath("$.data[0].stateRegion").exists())
                .andExpect(jsonPath("$.data[0].postalCode").exists())
                .andExpect(jsonPath("$.data[0].countryId").value(1))
                .andExpect(jsonPath("$.data[0].countryName").value("Colombia"))
                .andExpect(jsonPath("$.data[0].countryIso2").value("CO"))
                .andExpect(jsonPath("$.data[0].countryPhoneCodeId").value(1))
                .andExpect(jsonPath("$.data[0].phoneCode").value("+57"))
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].updatedAt").exists());
    }
}