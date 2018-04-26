package com.naukma.services;

import com.naukma.models.Transaction;
import com.naukma.models.TransactionStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public interface TransactionServiceInterface {
    @NotNull
    List<Transaction> getAllTransactions();

    @NotNull
    List<Transaction> getAllNotFinishedTransactions();

    @NotNull
    void updateTransactionStatus(@NotNull Transaction transaction, @NotNull TransactionStatus newStatus);
}
