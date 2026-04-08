package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;

public interface MovementService {
    MovementResponse createMovement(MovementCreateRequest request);
}
