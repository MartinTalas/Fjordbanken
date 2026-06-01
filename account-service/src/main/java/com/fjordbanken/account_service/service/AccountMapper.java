package com.fjordbanken.account_service.service;

import com.fjordbanken.account_service.dto.Account;
import com.fjordbanken.account_service.model.AccountEntity;

public interface AccountMapper extends MapperService<Account, AccountEntity> {
    String getCountryCode(String accountNumber);
}
