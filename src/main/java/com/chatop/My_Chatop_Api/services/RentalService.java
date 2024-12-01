package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired; // Annotation d'injection de dépendances
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    @Autowired // Injection du RR : permet à Spring de fournir une instance de RR
    private RentalRepository rentalRepository;

    // MTHD POUR RECUPÉRER TOUTES LES LOCATIONS
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll(); // Utilisation d'une mthd incluse dans l'interface JpaRepository
    }

    // MTHD POUR RECUPÉRER UNE LOCATION PAR SON ID
    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id); // Utilisation d'une mthd incluse dans l'interface JpaRepository
    }

    // MTHD POUR CREER UNE LOCATION
    public Rental createRental(Rental rental) {
        return rentalRepository.save(rental); // Utilisation d'une mthd incluse dans l'interface JpaRepository
    }

    // MTHD POUR MAJ UNE LOCATION EXISTANTE
    public Rental updateRental(Long id, Rental updatedRental) {

        Rental rental = getRentalById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id)); // Gère le cas où l'ID n'existe pas

        // Met à jour les champs de l'entité existante avec les nouvelles données
        rental.setName(updatedRental.getName());
        rental.setSurface(updatedRental.getSurface());
        rental.setPrice(updatedRental.getPrice());
        rental.setPicture(updatedRental.getPicture());
        rental.setDescription(updatedRental.getDescription());

        return rentalRepository.save(rental); // Sauvegarde l'entité mise à jour
    }

}
