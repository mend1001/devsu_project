package com.menditech.bank.customer.service.serviceImpl;

import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.entity.UserSessionEntity;
import com.menditech.bank.customer.repository.UserSessionRepository;
import com.menditech.bank.customer.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public void registerSession(ClientEntity client, String token, Long expiresInSeconds) {
        log.info("Registering new session for clientId={}, clientCode={}, expiresIn={}s",
                client.getId(), client.getCode(), expiresInSeconds);

        LocalDateTime now = LocalDateTime.now();
        String clientIp = getClientIp();
        String userAgent = request.getHeader("User-Agent");

        log.debug("Deactivating previous active sessions for clientId={}", client.getId());
        int deactivatedSessions = userSessionRepository.deactivateActiveSessionsByClientId(client.getId(), now, now);
        log.debug("Deactivated {} previous session(s) for clientId={}", deactivatedSessions, client.getId());

        UserSessionEntity session = UserSessionEntity.builder()
                .client(client)
                .sessionToken(token)
                .refreshToken(null)
                .loginAt(now)
                .lastActivityAt(now)
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .ipAddress(clientIp)
                .userAgent(userAgent)
                .deviceName(null)
                .isActive(true)
                .closedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserSessionEntity savedSession = userSessionRepository.save(session);
        log.info("Session registered successfully. SessionId={}, ClientId={}, IP={}, ExpiresAt={}",
                savedSession.getId(), client.getId(), clientIp, session.getExpiresAt());
    }

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String clientIp;

        if (StringUtils.hasText(xForwardedFor)) {
            clientIp = xForwardedFor.split(",")[0].trim();
            log.debug("Client IP extracted from X-Forwarded-For: {}", clientIp);
        } else {
            clientIp = request.getRemoteAddr();
            log.debug("Client IP extracted from RemoteAddr: {}", clientIp);
        }

        return clientIp;
    }
}