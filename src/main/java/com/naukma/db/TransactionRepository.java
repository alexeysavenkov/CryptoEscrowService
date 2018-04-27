package com.naukma.db;

import com.naukma.models.Transaction;
import com.naukma.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TransactionRepository {
    @PersistenceContext
    private EntityManager em;

    public void saveTransaction(Transaction transaction) {
        transaction.setTimeCreated(new java.sql.Date(new java.util.Date().getTime()));
        em.persist(transaction);
    }

    public List<Transaction> getTransactionsByUser(User user) {
        return em.createNamedQuery("getTransactionsByUser", Transaction.class).setParameter("userId", user.getId()).getResultList();
    }

    public List<Transaction> getAllTransactions() {
        return em.createNamedQuery("getAllTransactions", Transaction.class).getResultList();
    }

}
