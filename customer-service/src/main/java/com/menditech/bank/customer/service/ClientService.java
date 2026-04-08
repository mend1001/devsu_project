package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.ChangePasswordRequest;
import com.menditech.bank.customer.dto.request.ClientCreateRequest;
import com.menditech.bank.customer.dto.request.ClientUpdateRequest;
import com.menditech.bank.customer.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientResponse createClient(ClientCreateRequest request);
    ClientResponse updateClient(Long clientId, ClientUpdateRequest request);
    void deleteClient(Long clientId);
    ClientResponse getClientById(Long clientId);
    Page<ClientResponse> getAllClients(Pageable pageable);
    void changePassword(Long clientId, ChangePasswordRequest request);
}