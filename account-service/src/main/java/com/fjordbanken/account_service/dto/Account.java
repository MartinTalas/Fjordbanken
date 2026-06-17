package com.fjordbanken.account_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;
    private String customerName;
    private String accountNumber;
    private String countryCode;
    private String status;
}