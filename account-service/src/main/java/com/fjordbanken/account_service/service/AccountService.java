package com.fjordbanken.account_service.service;

import com.fjordbanken.account_service.dto.Account;
import com.fjordbanken.account_service.enums.AccountPrefix;
import com.fjordbanken.account_service.enums.AccountStatus;
import com.fjordbanken.account_service.exception.ResourceNotFoundException;
import com.fjordbanken.account_service.model.AccountEntity;
import com.fjordbanken.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final Random random = new SecureRandom();

    @Transactional
    public Account createAccount(Account accountDto) {
        if (accountRepository.existsByCustomerName(accountDto.getCustomerName())) {
            throw new IllegalStateException("Customer already has an active account.");
        }
        log.info("Creating a new banking account for customer: {}", accountDto.getCustomerName());

        AccountPrefix prefixEnum = AccountPrefix.fromString(accountDto.getCountryCode());
        String generatedAccountNumber;
        do {
            generatedAccountNumber = generateDynamicAccountNumber(prefixEnum);
        } while (accountRepository.existsByAccountNumber(generatedAccountNumber));
        AccountEntity entity = accountMapper.toEntity(accountDto);
        entity.setAccountNumber(generatedAccountNumber);
        AccountEntity savedEntity = accountRepository.save(entity);
        return accountMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(UUID id) {
        log.info("Fetching account details for ID: {}", id);

        return accountRepository.findByIdAndStatusNot(id, AccountStatus.CLOSED)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found or is inactive with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Account> getAllActiveAccounts() {
        log.info("Fetching all active accounts");

        return accountRepository.findAllByStatus(AccountStatus.ACTIVE).stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional
    public Account updateAccount(UUID id, Account accountDto) {
        log.info("Executing account update sequence for ID: {}", id);

        AccountEntity existingEntity = accountRepository.findByIdAndStatusNot(id, AccountStatus.CLOSED)
                .orElseThrow(() -> new ResourceNotFoundException("Account is inactive or missing with ID: " + id));

        existingEntity.setCustomerName(accountDto.getCustomerName());
        AccountEntity updatedEntity = accountRepository.save(existingEntity);
        return accountMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        log.info("Executing account deletion sequence for ID: {}", id);

        AccountEntity accountEntity = accountRepository.findByIdAndStatusNot(id, AccountStatus.CLOSED)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found or already closed"));

        accountEntity.setStatus(AccountStatus.CLOSED);
        accountRepository.save(accountEntity);
    }

    @Transactional
    public Account reactivateAccount(UUID id) {
        log.info("Attempting to reactivate account: {}", id);

        AccountEntity accountEntity = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        if (accountEntity.getStatus() != AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is not closed, cannot reactivate.");
        }

        accountEntity.setStatus(AccountStatus.ACTIVE);
        log.info("Account {} successfully reactivated", id);
        return accountMapper.toDto(accountRepository.save(accountEntity));
    }

    private String generateDynamicAccountNumber(AccountPrefix prefix) {
        int numericLength = switch (prefix) {
            case NORWEGIAN -> 9;
            case HUNGARIAN -> 24;
            case EUROPEAN -> 20;
            case INTERNATIONAL -> 15;
        };

        StringBuilder numericSuffix = new StringBuilder();
        for (int i = 0; i < numericLength; i++) {
            numericSuffix.append(random.nextInt(10));
        }

        return prefix.getPrefix() + numericSuffix;
    }
}