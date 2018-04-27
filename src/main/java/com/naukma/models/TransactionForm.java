package com.naukma.models;


import javax.validation.constraints.*;
import java.math.BigDecimal;

public class TransactionForm {

    @NotNull
    @Size(min=100, max=64000)
    private String termsOfAgreement;

    @NotNull
    @Size(min=1, max=100)
    private String cryptocurrency;

    @DecimalMin(value="0", inclusive=false)
    private BigDecimal amount;

    @NotNull
    private Integer isSender;

    @NotNull
    @Email
    private String anotherUserEmail;

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

    public Integer getIsSender() {
        return isSender;
    }

    public void setIsSender(Integer isSender) {
        this.isSender = isSender;
    }

    public String getAnotherUserEmail() {
        return anotherUserEmail;
    }

    public void setAnotherUserEmail(String anotherUserEmail) {
        this.anotherUserEmail = anotherUserEmail;
    }
}
