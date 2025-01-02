package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.dtos.RentalRequest;
import com.chatop.My_Chatop_Api.dtos.User.UserDto;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import com.chatop.My_Chatop_Api.tools.FileUploadUtils;
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

    public Rental saveRental(RentalRequest rental) throws IOException {
        Rental rentalToSave = new Rental();
        rentalToSave.setName(rental.getName());
        rentalToSave.setPrice(rental.getPrice());
        rentalToSave.setDescription(rental.getDescription());
        rentalToSave.setSurface(rental.getSurface());

        String picturePath = FileUploadUtils.saveFile(rental.getPicture());

        rentalToSave.setPicture(picturePath);
        rentalToSave.setCreated_at(LocalDate.now());
        rentalToSave.setUpdated_at(LocalDate.now());

        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaim("email");
        // Récupérer l'utilisateur à partir du service
        UserDto user = userService.getUserByEmail(email);

        rentalToSave.setOwner_id(user.getId());

        rentalToSave.setCreated_at(LocalDate.now());
        rentalToSave.setUpdated_at(LocalDate.now());

        return rentalRepository.save(rentalToSave);
    }

    public List<Rental> getRentals() {
        return rentalRepository.findAll();
    }

    public Rental getRental(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public Rental updateRental(Long id, RentalRequest rental) throws IOException {
        Rental rentalToUpdate = rentalRepository.findById(id).orElse(null);
        if (rentalToUpdate == null) {
            return null;
        }

        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaim("email");
        // Récupérer l'utilisateur à partir du service
        UserDto user = userService.getUserByEmail(email);

        if (!rentalToUpdate.getOwner_id().equals(user.getId()) ) {
            throw new AccessDeniedException("User is not authorized"); // Lever une exception standard pour retourner 401/403
        }

        rentalToUpdate.setName(rental.getName());
        rentalToUpdate.setPrice(rental.getPrice());
        rentalToUpdate.setDescription(rental.getDescription());
        rentalToUpdate.setSurface(rental.getSurface());
        rentalToUpdate.setUpdated_at(LocalDate.now());

        return rentalRepository.save(rentalToUpdate);
    }

}
