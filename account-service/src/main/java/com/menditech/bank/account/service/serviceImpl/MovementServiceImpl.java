package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;

public interface MovementServiceImpl {
    MovementResponse createMovement(MovementCreateRequest request);
}
