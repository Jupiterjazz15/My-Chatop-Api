package com.chatop.My_Chatop_Api.repositories;

import com.chatop.My_Chatop_Api.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Trouver les messages par rentalId
    List<Message> findByRentalId(Long rental_id);

    // Trouver les messages par userId (expéditeur/destinataire)
    List<Message> findByUserId(Long user_id);

    // Trouver un message par son ID
    Optional<Message> findById(Long id);
}
