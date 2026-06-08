package com.fjordbanken.account_service.repository;

import com.fjordbanken.account_service.enums.AccountStatus;
import com.fjordbanken.account_service.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    List<AccountEntity> findAllByStatus(AccountStatus status);

    Optional<AccountEntity> findByIdAndStatusNot(UUID id, AccountStatus status);

    boolean existsByAccountNumber(String generatedAccountNumber);

    boolean existsByCustomerName(String customerName);
}