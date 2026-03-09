package com.menditech.bank.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Size(max = 255, message = "Current password must not exceed 255 characters")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(max = 255, message = "New password must not exceed 255 characters")
    private String newPassword;
}