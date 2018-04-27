package com.naukma.services;

import com.naukma.db.DisputeRepository;
import com.naukma.db.TransactionRepository;
import com.naukma.models.Dispute;
import com.naukma.models.Transaction;
import com.naukma.models.TransactionStatus;
import com.naukma.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Service
public class DisputeService {

    @Autowired
    DisputeRepository disputeRepository;

    public void createDispute(@NotNull Transaction transaction) {
        Dispute dispute = new Dispute();
        dispute.setTransactionId(transaction.getId());
        disputeRepository.saveDispute(dispute);
    }

    public void resolveDispute(Dispute dispute, BigDecimal amountRefunded) {
        dispute.setTimeResolved(new java.sql.Date(new java.util.Date().getTime()));
        disputeRepository.saveDispute(dispute);
    }
}
