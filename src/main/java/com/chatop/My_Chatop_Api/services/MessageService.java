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
        messageToSave.setUser_id(message.getUser_id());
        messageToSave.setRental_id(message.getRental_id());
        messageToSave.setCreated_at(LocalDate.now());
        messageToSave.setUpdated_at(LocalDate.now());

        return messageRepository.save(messageToSave);
    }

}
