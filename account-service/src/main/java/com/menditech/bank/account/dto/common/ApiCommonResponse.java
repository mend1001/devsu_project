package com.menditech.bank.account.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCommonResponse<T> {

    private int httpStatus;
    private String status;
    private LocalDateTime timestamp;
    private String message;
    private T data;
}