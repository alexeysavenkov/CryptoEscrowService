package com.naukma.models;


import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class Transaction {

    private Integer id;

    @Size(min=100, max=64000)
    private String termsOfAgreement;

    @Size(min=1, max=100)
    private String cryptocurrency;

    @DecimalMin(value="0", inclusive=false)
    private BigDecimal amount;


    @NotNull
    private TransactionStatus status;


    // Sender and recipient do not need to log in
    // Type of user is detemined by url in browser
    // i.e. url of transaction is different for sender and for recipient
    //       and they need to save it and not to share it

    @Size(min=100)
    private String senderSecretUrl;

    @Size(min=100)
    private String recipientSecretUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTermsOfAgreement() {
        return termsOfAgreement;
    }

    public void setTermsOfAgreement(String termsOfAgreement) {
        this.termsOfAgreement = termsOfAgreement;
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

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getSenderSecretUrl() {
        return senderSecretUrl;
    }

    public void setSenderSecretUrl(String senderSecretUrl) {
        this.senderSecretUrl = senderSecretUrl;
    }

    public String getRecipientSecretUrl() {
        return recipientSecretUrl;
    }

    public void setRecipientSecretUrl(String recipientSecretUrl) {
        this.recipientSecretUrl = recipientSecretUrl;
    }
}
