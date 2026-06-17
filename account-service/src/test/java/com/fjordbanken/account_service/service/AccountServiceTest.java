package com.fjordbanken.account_service.service;

import com.fjordbanken.account_service.dto.Account;
import com.fjordbanken.account_service.enums.AccountPrefix;
import com.fjordbanken.account_service.enums.AccountStatus;
import com.fjordbanken.account_service.model.AccountEntity;
import com.fjordbanken.account_service.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    private final String TEST_CUSTOMER_NAME = "Ola Nordmann";
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_ShouldThrowException_WhenCustomerAlreadyHasAccount() {
        Account requestDto = Account.builder()
                .customerName(TEST_CUSTOMER_NAME)
                .countryCode(AccountPrefix.NORWEGIAN.getPrefix())
                .build();

        when(accountRepository.existsByCustomerName(TEST_CUSTOMER_NAME)).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer already has an active account.");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccount_ShouldSaveAndReturnAccount_WhenValid() {
        Account requestDto = Account.builder()
                .customerName(TEST_CUSTOMER_NAME)
                .countryCode(AccountPrefix.NORWEGIAN.getPrefix())
                .build();
        AccountEntity mockEntity = AccountEntity.builder().customerName(TEST_CUSTOMER_NAME).build();
        AccountEntity savedEntity = AccountEntity.builder()
                .id(UUID.randomUUID())
                .customerName(TEST_CUSTOMER_NAME)
                .accountNumber("NO123456789")
                .status(AccountStatus.PENDING)
                .build();
        Account expectedDto = Account.builder()
                .countryCode(AccountPrefix.NORWEGIAN.getPrefix())
                .customerName(TEST_CUSTOMER_NAME)
                .accountNumber("NO123456789")
                .status("PENDING")
                .build();

        when(accountRepository.existsByCustomerName(TEST_CUSTOMER_NAME)).thenReturn(false);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountMapper.toEntity(requestDto)).thenReturn(mockEntity);
        when(accountRepository.save(mockEntity)).thenReturn(savedEntity);
        when(accountMapper.toDto(savedEntity)).thenReturn(expectedDto);

        Account result = accountService.createAccount(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getCountryCode()).startsWith("NO");
        assertThat(result.getStatus()).isEqualTo("PENDING");

        verify(accountRepository, times(1)).save(mockEntity);
    }
}
