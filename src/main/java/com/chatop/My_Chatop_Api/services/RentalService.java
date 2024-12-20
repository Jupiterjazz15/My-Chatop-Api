package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    // MTHD POUR RECUPÉRER TOUTES LES LOCATIONS (incluse dans l'interface JpaRepo)
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    // MTHD POUR RECUPÉRER UNE LOCATION PAR SON ID (incluse dans l'interface JpaRepo)
    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    // MTHD POUR CREER UNE LOCATION (incluse dans l'interface JpaRepo)
    public Rental createRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    // MTHD POUR MAJ UNE LOCATION EXISTANTE
    public Rental updateRental(Long id, Rental updatedRental) {

        Optional<Rental> existingRental = rentalRepository.findById(id);

        if (existingRental.isEmpty()) {
            throw new RuntimeException("Rental not found with id: " + id);
        }

        // MAJ les champs de l'objet Rental existant avec les nouvelles données
        Rental rental = existingRental.get();
        rental.setName(updatedRental.getName());
        rental.setSurface(updatedRental.getSurface());
        rental.setPrice(updatedRental.getPrice());
        rental.setDescription(updatedRental.getDescription());
        rental.setPicture(updatedRental.getPicture());

        return rentalRepository.save(rental);
    }


}
