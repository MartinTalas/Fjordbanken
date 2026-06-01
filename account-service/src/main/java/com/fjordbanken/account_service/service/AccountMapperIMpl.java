package com.fjordbanken.account_service.service;

import com.fjordbanken.account_service.dto.Account;
import com.fjordbanken.account_service.model.AccountEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountMapperIMpl implements AccountMapper{
    @Override
    public AccountEntity toEntity(Account dto) {
        if (dto == null) {
            return null;
        }

        return AccountEntity.builder()
                .id(dto.getId())
                .customerName(dto.getCustomerName())
                .accountNumber(dto.getAccountNumber())
                .build();
    }

    @Override
    public Account toDto(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        return Account.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .accountNumber(entity.getAccountNumber())
                .countryCode(getCountryCode(entity.getAccountNumber()))
                .build();
    }

    @Override
    public String getCountryCode(String accountNumber) {
        return accountNumber.split("\\d", 2)[0];
    }
}
