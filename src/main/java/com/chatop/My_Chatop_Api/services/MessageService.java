package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Méthode pour créer un message
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    // Méthode pour récupérer un message par son ID
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    // Méthode pour récupérer tous les messages d'un rentalId
    public List<Message> getMessagesByRentalId(Long rentalId) {
        return messageRepository.findByRentalId(rentalId);
    }

    // Méthode pour récupérer tous les messages d'un utilisateur
    public List<Message> getMessagesByUserId(Long userId) {
        return messageRepository.findByUserId(userId);
    }
}
