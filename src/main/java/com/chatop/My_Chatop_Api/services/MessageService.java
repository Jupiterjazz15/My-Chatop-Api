package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.MessageRepository;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentalRepository rentalRepository;

    public Message createMessage(Message message) {
        //Rental rental = rentalRepository.findById(rentalId)
                //.orElseThrow(() -> new RuntimeException("Rental not found"));

       // User user = userRepository.findById(userId)
               // .orElseThrow(() -> new RuntimeException("User not found"));

        // Créer un nouveau message avec les objets récupérés et le contenu
        //Message message = new Message(user, rental, content);

        // Sauvegarder le message dans la base de données
        return messageRepository.save(message);
    }

}

