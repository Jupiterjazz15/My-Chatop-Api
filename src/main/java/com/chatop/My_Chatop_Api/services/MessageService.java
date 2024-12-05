package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.MessageRepository;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RentalRepository rentalRepository;

    public Message createMessage(String content, Long rentalId, User user) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        Message message = new Message();
        message.setMessage(content);
        message.setUser(user);
        message.setRental(rental);

        return messageRepository.save(message);
    }
}

