package com.naukma.services;

import com.naukma.models.Message;
import com.naukma.models.Transaction;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public interface MessageServiceInterface {
    @NotNull
    List<Message> getMessagesByTransaction(@NotNull Transaction transaction);

    @NotNull
    Transaction getTransactionByMessage(@NotNull Message message);
}
