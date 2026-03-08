package com.menditech.bank.customer.service;

import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.entity.UserSessionEntity;
import com.menditech.bank.customer.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final HttpServletRequest request;

    @Transactional
    public void registerSession(ClientEntity client, String token, Long expiresInSeconds) {
        LocalDateTime now = LocalDateTime.now();

        userSessionRepository.deactivateActiveSessionsByClientId(client.getId(), now, now);

        UserSessionEntity session = UserSessionEntity.builder()
                .client(client)
                .sessionToken(token)
                .refreshToken(null)
                .loginAt(now)
                .lastActivityAt(now)
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .ipAddress(getClientIp())
                .userAgent(request.getHeader("User-Agent"))
                .deviceName(null)
                .isActive(true)
                .closedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userSessionRepository.save(session);
    }

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
