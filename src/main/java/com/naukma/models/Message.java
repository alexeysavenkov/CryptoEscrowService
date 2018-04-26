package com.naukma.models;

import javax.validation.constraints.NotNull;

public class Message {

    private Integer id;

    @NotNull
    private Integer transactionId;

    enum Sender {
        MoneySender,
        MoneyRecipient,
        Admin
    }

    @NotNull
    Sender sender;

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
}
