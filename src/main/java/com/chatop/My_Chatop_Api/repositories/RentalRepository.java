package com.chatop.My_Chatop_Api.repositories;

import com.chatop.My_Chatop_Api.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository; // Interface JPA pour la gestion des entités
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // MTHD PR TROUVER UN RENTAL À PARTIR DE L'ID DU PROPRIETAIRE
    // Notation pointée (Owner.Id) pr naviguer dans la relation @ManyToOne et accéder à l'attribut id de l'entité User
    //List<Rental> findByOwner_Id(Long ownerId);

    // MTHD PR TROUVER UN RENTAL À PARTIR DU NOM DU RENTAL
   // List<Rental> findByNameContaining(String name);
}
