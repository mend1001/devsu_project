package com.menditech.bank.customer.controller;

import com.menditech.bank.customer.dto.common.ApiResponse;
import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.request.ClientUpdateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;
import com.menditech.bank.customer.service.ClientService;
import com.menditech.bank.customer.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(@Valid @RequestBody ClientCreateRequest request) {
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(HttpStatus.CREATED, "Client created successfully", response));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable Long clientId) {
        ClientResponse response = clientService.getClientById(clientId);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Client retrieved successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllClients() {
        List<ClientResponse> response = clientService.getAllClients();
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Clients retrieved successfully", response)
        );
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable Long clientId,
            @Valid @RequestBody ClientUpdateRequest request
    ) {
        ClientResponse response = clientService.updateClient(clientId, request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Client updated successfully", response)
        );
    }
}
