package com.naukma.models;


import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name="dispute")
public class Dispute {

    @Id
    @Column(name="transaction_id")
    @NotNull
    private Integer transactionId;

    @Column(name="amount_refunded")
    private BigDecimal amountRefunded;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="time_created")
    @NotNull
    private Date timeCreated;

    @Column(name="time_resolved")
    private Date timeResolved;

    @OneToOne
    @JoinColumn(name="transactionId")
    private Transaction transaction;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmountRefunded() {
        return amountRefunded;
    }

    public void setAmountRefunded(BigDecimal amountRefunded) {
        this.amountRefunded = amountRefunded;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeResolved() {
        return timeResolved;
    }

    public void setTimeResolved(Date timeResolved) {
        this.timeResolved = timeResolved;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
