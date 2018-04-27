package com.naukma.models;

import org.ocpsoft.prettytime.PrettyTime;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name="message")
@NamedQueries({
        @NamedQuery(name="getMessagesByTransaction", query="SELECT m FROM Message m WHERE m.transactionId = :transactionId ORDER BY m.timeCreated DESC")
})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name="transaction_id")
    private Integer transactionId;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @ManyToOne
    @JoinColumn(name="transaction_id", insertable = false, updatable = false)
    private Transaction transaction;

    @NotNull
    @Column(name="user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @NotEmpty
    private String message;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="time_created")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date timeCreated;

    public String getTimeCreatedFromNow() {
        PrettyTime p = new PrettyTime();
        return p.format(timeCreated);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public java.util.Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isByMoneySender() {
        return transaction.getSenderId().equals(userId);
    }

    public boolean isByMoneyRecepient() {
        return transaction.getRecipientId().equals(userId);
    }

    public boolean isByAdmin() {
        return !isByMoneyRecepient() && !isByMoneySender();
    }
}
