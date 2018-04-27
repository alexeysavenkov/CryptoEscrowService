package com.naukma.db;

import com.naukma.models.Dispute;
import com.naukma.models.Message;
import com.naukma.models.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class MessageRepository {
    @PersistenceContext
    private EntityManager em;

    public void saveMessage(Message message) {
        em.persist(message);
    }

    public List<Message> getMessagesByTransaction(Transaction transaction) {
        return em.createNamedQuery("getMessagesByTransaction", Message.class).setParameter("transactionId", transaction.getId()).getResultList();
    }

}
