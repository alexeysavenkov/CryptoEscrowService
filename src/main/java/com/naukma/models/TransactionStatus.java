package com.naukma.models;

public enum TransactionStatus {
    WaitingForTermAgreement,
    WaitingForPaymentByMoneySender,
    WaitingForItemOrServiceByMoneyRecipient,
    CheckingItemOrServiceByMoneySender,
    FinishedWithoutDispute,

    DisputeStarted,
    WaitingForDecisionByAdmin,
    DisputeFinished
}
