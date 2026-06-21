package com.fjordbanken.transaction_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record Transaction(
        UUID id,
        String transactionNumber,
        String senderName,
        String status,         // e.g., "SUCCESS", "PENDING", "REJECTED"
        String transactionType, // e.g., "TRANSFER", "DEPOSIT"
        Instant createdAt,
        Instant settledAt,
        String failureReason,

        @NotNull(message = "Source account ID is required")
        UUID sourceAccountId,

        @NotBlank(message = "Target account number is required")
        String targetAccountNumber,

        @NotBlank(message = "Receiver name is required")
        String receiverName,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency,

        String reference
) {}