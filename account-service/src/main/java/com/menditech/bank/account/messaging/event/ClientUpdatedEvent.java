package com.menditech.bank.account.messaging.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUpdatedEvent {

    private Long clientId;
    private Long personId;
    private Long roleId;
    private String clientCode;
    private String fullName;
    private String identificationNumber;
    private String email;
    private String phoneNumber;
    private String status;
    private Boolean isActive;
    private LocalDateTime eventDate;
}