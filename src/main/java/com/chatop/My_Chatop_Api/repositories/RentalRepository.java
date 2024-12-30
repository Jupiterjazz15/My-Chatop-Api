package com.chatop.My_Chatop_Api.repositories;

import com.chatop.My_Chatop_Api.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository; // Interface JPA pour la gestion des entités
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

}
