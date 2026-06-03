package com.fjordbanken.account_service.service;

import com.fjordbanken.account_service.dto.Account;
import com.fjordbanken.account_service.enums.AccountPrefix;
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
        log.info("Creating a new banking account for customer: {}", accountDto.getCustomerName());

        AccountPrefix prefixEnum = AccountPrefix.fromString(accountDto.getCountryCode());
        AccountEntity entity = accountMapper.toEntity(accountDto);
        entity.setAccountNumber(generateDynamicAccountNumber(prefixEnum));
        AccountEntity savedEntity = accountRepository.save(entity);
        return accountMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(UUID id) {
        log.info("Fetching account details for ID: {}", id);

        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        log.info("Fetching all registered accounts");

        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional
    public Account updateAccount(UUID id, Account accountDto) {
        log.info("Executing account update sequence for ID: {}", id);

        AccountEntity existingEntity = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
        /* Usually, we can NOT allow changing the accountNumber or countryCode after creation in a bank system,
        so we only update mutable fields like the name here. */
        existingEntity.setCustomerName(accountDto.getCustomerName());AccountEntity updatedEntity = accountRepository.save(existingEntity);
        return accountMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        log.info("Executing account deletion sequence for ID: {}", id);

        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with ID: " + id);
        }

        accountRepository.deleteById(id);
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