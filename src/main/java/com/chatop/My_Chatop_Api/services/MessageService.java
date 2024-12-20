package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.dtos.MessageRequest;
import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;


    public Message saveMessage(MessageRequest message) {

        Message messageToSave = new Message();
        messageToSave.setMessage(message.getMessage());
        messageToSave.setUserId(message.getUserId());
        messageToSave.setRentalId(message.getRentalId());
        messageToSave.setCreatedAt(LocalDate.now());
        messageToSave.setUpdatedAt(LocalDate.now());

        return messageRepository.save(messageToSave);
    }

}
