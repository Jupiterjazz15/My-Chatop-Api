package com.chatop.My_Chatop_Api.repositories;

import com.chatop.My_Chatop_Api.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Trouver les messages par rentalId
    List<Message> findByRentalId(Long rentalId);

    // Trouver les messages par userId (expéditeur/destinataire)
    List<Message> findByUserId(Long userId);

    // Trouver un message par son ID
    Message findById(long id);

    // Trouver les messages non lus pour un utilisateur
    List<Message> findByUserIdAndReadStatusFalse(Long userId);

}
