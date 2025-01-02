package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.dtos.RentalRequest;
import com.chatop.My_Chatop_Api.dtos.User.UserResponse;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    // Méthode qui prend un DTO pour construire un objet Rental, sauvegarder l'image, récupérer les infos de l'utilisateur authentifié, ajouter un createdAt
    public Rental saveRental(RentalRequest rental) throws IOException {
        Rental rentalToSave = new Rental();
        rentalToSave.setName(rental.getName());
        rentalToSave.setPrice(rental.getPrice());
        rentalToSave.setDescription(rental.getDescription());
        rentalToSave.setSurface(rental.getSurface());

        // Sauvegarder le fichier image
        String picturePath = fileStorageService.saveFile(rental.getPicture());
        rentalToSave.setPicture(picturePath);

        // Dates de création et mise à jour
        rentalToSave.setCreatedAt(LocalDate.now());
        rentalToSave.setUpdatedAt(LocalDate.now());

        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier que l'utilisateur est authentifié
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        // Récupérer l'utilisateur à partir du service
        UserResponse user = userService.getUserByEmail(email);

        // Assigner l'ID de l'utilisateur authentifié comme propriétaire
        rentalToSave.setOwnerId(user.getId());

        // Sauvegarder la location
        return rentalRepository.save(rentalToSave);
    }

    // Méthode pour récupérer toutes les locations
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    // Méthode pour récupérer une location par son ID
    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    // Méthode pour mettre à jour une location
    public Rental updateRental(Long id, RentalRequest rentalRequest) throws IOException {
        Rental rentalToUpdate = rentalRepository.findById(id).orElse(null);

        // Vérifier si la location existe
        if (rentalToUpdate == null) {
            return null;
        }

        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier que l'utilisateur est authentifié
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        // Récupérer l'utilisateur à partir du service
        UserResponse user = userService.getUserByEmail(email);

        // Vérifier que l'utilisateur est le propriétaire de la location
        if (!rentalToUpdate.getOwnerId().equals(user.getId())) {
            throw new AccessDeniedException("User is not authorized to update this rental"); // Lever une exception si non autorisé
        }

        // Mettre à jour les informations de la location
        rentalToUpdate.setName(rentalRequest.getName());
        rentalToUpdate.setSurface(rentalRequest.getSurface());
        rentalToUpdate.setPrice(rentalRequest.getPrice());
        rentalToUpdate.setDescription(rentalRequest.getDescription());

        // Gestion de l'image si une nouvelle image est envoyée
        if (rentalRequest.getPicture() != null && !rentalRequest.getPicture().isEmpty()) {
            String picturePath = fileStorageService.saveFile(rentalRequest.getPicture());
            rentalToUpdate.setPicture(picturePath);
        }

        // Mettre à jour la date de mise à jour
        rentalToUpdate.setUpdatedAt(LocalDate.now());

        // Sauvegarder les changements
        return rentalRepository.save(rentalToUpdate);
    }
}
