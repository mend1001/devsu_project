package com.menditech.bank.customer.controller;

import com.menditech.bank.customer.dto.common.ApiCommonResponse;
import com.menditech.bank.customer.dto.request.ChangePasswordRequest;
import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.request.ClientUpdateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;
import com.menditech.bank.customer.service.ClientService;
import com.menditech.bank.customer.util.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Operations related to client management")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Create a new client", description = "Creates a new client and associated person information in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Client already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiCommonResponse<ClientResponse>> createClient(
            @Parameter(description = "Client creation request body", required = true)
            @Valid @RequestBody ClientCreateRequest request) {
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(HttpStatus.CREATED, "Client created successfully", response));
    }

    @Operation(summary = "Get client by ID", description = "Retrieves a specific client using its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{clientId}")
    public ResponseEntity<ApiCommonResponse<ClientResponse>> getClientById(
            @Parameter(description = "Client identifier", example = "1")
            @PathVariable Long clientId) {
        ClientResponse response = clientService.getClientById(clientId);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Client retrieved successfully", response)
        );
    }

    @Operation(summary = "Get all clients", description = "Returns the list of all registered clients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ApiCommonResponse<List<ClientResponse>>> getAllClients() {
        List<ClientResponse> response = clientService.getAllClients();
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Clients retrieved successfully", response)
        );
    }

    @Operation(summary = "Update client", description = "Updates the information of an existing client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{clientId}")
    public ResponseEntity<ApiCommonResponse<ClientResponse>> updateClient(
            @Parameter(description = "Client identifier", example = "1")
            @PathVariable Long clientId,
            @Parameter(description = "Client update request body", required = true)
            @Valid @RequestBody ClientUpdateRequest request
    ) {
        ClientResponse response = clientService.updateClient(clientId, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Client updated successfully", response)
        );
    }

    @Operation(summary = "Deactivate client", description = "Performs a logical deletion by deactivating the client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{clientId}")
    public ResponseEntity<ApiCommonResponse<String>> deleteClient(
            @Parameter(description = "Client identifier", example = "1")
            @PathVariable Long clientId
    ) {
        clientService.deleteClient(clientId);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Client deactivated successfully", "OK")
        );
    }

    @Operation(summary = "Change client password", description = "Updates only the client password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid current password"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PatchMapping("/{clientId}/password")
    public ResponseEntity<ApiCommonResponse<String>> changePassword(
            @Parameter(description = "Client identifier", example = "1")
            @PathVariable Long clientId,
            @Parameter(description = "Password change request body", required = true)
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        clientService.changePassword(clientId, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Password updated successfully", "OK")
        );
    }
}
