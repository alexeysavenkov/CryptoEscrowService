package com.naukma.db;

import com.naukma.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Transactional
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public User saveUser(User user) {
        em.persist(user);
        return findByEmail(user.getEmail()).get();
    }

    public Optional<User> findByEmail(String email) {
        return em.createNamedQuery("findByEmail", User.class).setParameter("email", email).getResultList().stream().findFirst();
    }
}
