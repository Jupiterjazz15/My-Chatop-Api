package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.dtos.RentalRequest;
import com.chatop.My_Chatop_Api.dtos.User.UserResponse;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    // MTHD qui prend un DTO pr construire un objet Rental, sauvegarder l'image, récupérer les infos du user authentifié, ajouter un created at
    public Rental saveRental(RentalRequest rental) throws IOException {
        Rental rentalToSave = new Rental();
        rentalToSave.setName(rental.getName());
        rentalToSave.setPrice(rental.getPrice());
        rentalToSave.setDescription(rental.getDescription());
        rentalToSave.setSurface(rental.getSurface());

        String picturePath = fileStorageService.saveFile(rental.getPicture());

        rentalToSave.setPicture(picturePath);
        rentalToSave.setCreatedAt(LocalDate.now());
        rentalToSave.setUpdatedAt(LocalDate.now());

        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaim("email");
        // Récupérer l'utilisateur à partir du service
        UserResponse user = userService.getUserByEmail(email);

        rentalToSave.setOwnerId(user.getId());

        rentalToSave.setCreatedAt(LocalDate.now());
        rentalToSave.setUpdatedAt(LocalDate.now());

        return rentalRepository.save(rentalToSave);
    }

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
    public Rental updateRental(Long id, RentalRequest rentalRequest) throws IOException {
        Optional<Rental> existingRental = rentalRepository.findById(id);

        if (existingRental.isEmpty()) {
            throw new RuntimeException("Rental not found with id: " + id);
        }

        Rental rental = existingRental.get();
        rental.setName(rentalRequest.getName());
        rental.setSurface(rentalRequest.getSurface());
        rental.setPrice(rentalRequest.getPrice());
        rental.setDescription(rentalRequest.getDescription());

        // Gestion de l'image si une nouvelle image est envoyée
        if (rentalRequest.getPicture() != null && !rentalRequest.getPicture().isEmpty()) {
            String picturePath = fileStorageService.saveFile(rentalRequest.getPicture());
            rental.setPicture(picturePath);
        }

        return rentalRepository.save(rental);
    }

}
