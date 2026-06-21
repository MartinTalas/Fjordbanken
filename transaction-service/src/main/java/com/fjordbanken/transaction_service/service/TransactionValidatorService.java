package com.fjordbanken.transaction_service.service;

import com.fjordbanken.transaction_service.dto.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidatorService {

    public boolean pseudoValidation(Transaction transaction){
        return true;
    }
}
