package com.fjordbanken.account_service.service;

import com.fjordbanken.account_service.dto.Account;
import com.fjordbanken.account_service.enums.AccountStatus;
import com.fjordbanken.account_service.model.AccountEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper extends MapperService<Account, AccountEntity> {
    @Override
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    AccountEntity toEntity(Account dto);

    @Override
    @Mapping(target = "countryCode", source = "accountNumber", qualifiedByName = "extractCountryCode")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    Account toDto(AccountEntity entity);

    @Named("extractCountryCode")
    default String extractCountryCode(String accountNumber) {
        if (accountNumber == null) return null;
        return accountNumber.split("\\d", 2)[0];
    }

    @Named("stringToStatus")
    default AccountStatus stringToStatus(String status) {
        if (status == null) return AccountStatus.PENDING;
        return AccountStatus.valueOf(status.toUpperCase());
    }

    @Named("statusToString")
    default String statusToString(AccountStatus status) {
        if (status == null) return null;
        return status.name();
    }

    void updateEntityFromDto(Account accountDto, @MappingTarget AccountEntity existingEntity);
}
