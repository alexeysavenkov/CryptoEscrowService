package com.naukma.services;

import com.naukma.db.MessageRepository;
import com.naukma.models.Message;
import com.naukma.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    public List<Message> getMessagesByTransaction(Transaction transaction) {
        return messageRepository.getMessagesByTransaction(transaction);
    }

    public void createMessage(Message message) {
        messageRepository.saveMessage(message);
    }
}

