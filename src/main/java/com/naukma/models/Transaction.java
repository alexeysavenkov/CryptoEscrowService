package com.naukma.models;


import net.bytebuddy.implementation.bind.annotation.Default;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name="transaction")
@NamedQueries({
        @NamedQuery(name = "getTransactionsByUser", query = "SELECT t FROM Transaction t WHERE t.senderId = :userId OR t.recipientId = :userId ORDER BY t.timeCreated DESC"),
        @NamedQuery(name = "getAllTransactions", query = "SELECT t FROM Transaction t ORDER BY t.timeCreated DESC")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Size(min=1, max=20)
    private String cryptocurrency;

    @DecimalMin(value="0", inclusive=false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "sender_id")
    private Integer senderId;

    @NotNull
    @Column(name = "recipient_id")
    private Integer recipientId;


    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;


    @Size(min=0, max=64000)
    @NotNull
    @Column(name = "terms_of_agreement")
    private String termsOfAgreement;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="time_created")
    private Date timeCreated;

    @ManyToOne
    @JoinColumn(name="sender_id", insertable = false, updatable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name="recipient_id", insertable = false, updatable = false)
    private User recipient;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(String cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getTermsOfAgreement() {
        return termsOfAgreement;
    }

    public void setTermsOfAgreement(String termsOfAgreement) {
        this.termsOfAgreement = termsOfAgreement;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}
