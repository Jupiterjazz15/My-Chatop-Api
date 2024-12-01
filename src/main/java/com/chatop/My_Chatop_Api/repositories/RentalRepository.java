package com.chatop.My_Chatop_Api.repositories;

import com.chatop.My_Chatop_Api.models.Rental; // Import du modèle Rental
import org.springframework.data.jpa.repository.JpaRepository; // Interface JPA pour la gestion des entités
import org.springframework.stereotype.Repository; // Annotation pour indiquer que c'est un Repository

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // Méthode pour trouver tous les rentals d'un utilisateur (déduite par Spring Data JPA)
    List<Rental> findByOwnerId(Long owner_id);

    // Méthode pour rechercher un rental par son nom ou partie (déduite par Spring Data JPA)
    List<Rental> findByNameContaining(String name);

}
