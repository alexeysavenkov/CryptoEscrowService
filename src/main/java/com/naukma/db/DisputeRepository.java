package com.naukma.db;

import com.naukma.models.Dispute;
import com.naukma.models.Transaction;
import com.naukma.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;

@Repository
@Transactional
public class DisputeRepository {
    @PersistenceContext
    private EntityManager em;

    public void saveDispute(Dispute dispute) {
        em.persist(dispute);
    }

}
