package com.naukma.services;

import com.naukma.db.TransactionRepository;
import com.naukma.models.Transaction;
import com.naukma.models.TransactionStatus;
import com.naukma.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public void createTransaction(@NotNull Transaction transaction) {
        transaction.setTimeCreated(new java.sql.Date(new java.util.Date().getTime()));
        transactionRepository.saveTransaction(transaction);
    }

    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository.getTransactionsByUser(user);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    public int countTransactionsByUser(User user) {
        return getTransactionsByUser(user).size();
    }

    public Transaction getById(int id) {
        return transactionRepository.getById(id);
    }

    public void updateLastTimeUpdated(Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
    }


    public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus newStatus) {
        transaction.setStatus(newStatus);
        transactionRepository.saveTransaction(transaction);
        return transaction;
    }
}
