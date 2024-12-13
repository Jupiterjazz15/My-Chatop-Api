package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import com.chatop.My_Chatop_Api.services.RentalService;
import com.chatop.My_Chatop_Api.services.FileStorageService;
import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import com.chatop.My_Chatop_Api.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AuthTokenFilter authTokenFilter;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);

    @GetMapping("") // RECUPERER TOUTES LES LOCATIONS
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals); // Retourne un statut 200 avec la liste des locations
    }


    @GetMapping("/{id}") // RECUPERER UNE LOCATION VIA SON ID
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        Optional<Rental> rental = rentalService.getRentalById(id);

        if (rental.isPresent()) {
            return ResponseEntity.ok(rental.get()); // Retourne l'objet Rental si trouvé
        } else {
            return ResponseEntity.notFound().build(); // Retourne un 404 si le Rental n'existe pas
        }
    }

    @PostMapping("") // CREER UN RENTAL
    public ResponseEntity<?> createRental(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "surface") BigDecimal surface,
            @RequestParam(value = "price") BigDecimal price,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam(value = "owner_id") Long owner_id,
            HttpServletRequest request) {

        System.out.println("MYNAMEIS" + name + "MYOWNERIDIS" + owner_id);

        try {
            // Extraire le JWT depuis la requête
            String jwt = this.authTokenFilter.parseJwt(request);
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token.");
            }

            // Extraire l'email de l'utilisateur depuis le JWT et rechercher l'utilisateur dans la base
            String userEmail = jwtUtils.getUserNameFromJwtToken(jwt);
            Optional<User> user = this.userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Vérifier si l'ownerId existe dans la base de données
            Optional<User> owner = userRepository.findById(owner_id);
            if (owner.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Owner not found.");
            }

            // Vérifier que l'utilisateur authentifié correspond à l'ownerId dans la requête
            if (!user.get().getId().equals(owner_id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only create a rental for yourself.");
            }

            // Sauvegarder l'image si présente
            String picturePath = null;
            if (picture != null && !picture.isEmpty()) {
                picturePath = fileStorageService.saveFile(picture);
            }

            // Construire un objet Rental
            Rental rental = new Rental();
            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setDescription(description);
            rental.setPicture(picturePath);
            rental.setOwnerId(owner_id); // Associer le rental à l'utilisateur authentifié

            // Sauvegarder le Rental
            Rental createdRental = rentalService.createRental(rental);

            // Retourner le Rental créé
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);

        } catch (Exception e) {
            // Log détaillé de l'exception pour le débogage
            logger.error("Erreur lors de la création du rental : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }


    @PutMapping("/{id}") // MAJ UN RENTAL
    public ResponseEntity<?> updateRental(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surface", required = false) BigDecimal surface,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            HttpServletRequest request) {

        try {
            // Extraire le JWT depuis la requête
            String jwt = this.authTokenFilter.parseJwt(request);
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token.");
            }

            // Extraire l'email de l'utilisateur depuis le JWT
            String userEmail = jwtUtils.getUserNameFromJwtToken(jwt);
            Optional<User> user = this.userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Rechercher le Rental par son ID
            Optional<Rental> existingRental = rentalService.getRentalById(id);
            if (existingRental.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental not found.");
            }

            Rental rental = existingRental.get();

            // Vérifier que l'utilisateur authentifié est bien le propriétaire du Rental
            if (!rental.getOwnerId().equals(user.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own rentals.");
            }

            // Mettre à jour les champs du Rental si des valeurs sont fournies
            if (name != null) rental.setName(name);
            if (surface != null) rental.setSurface(surface);
            if (price != null) rental.setPrice(price);
            if (description != null) rental.setDescription(description);

            // Gérer la mise à jour de l'image
            if (picture != null && !picture.isEmpty()) {
                String picturePath = fileStorageService.saveFile(picture);
                rental.setPicture(picturePath);
            }

            // Sauvegarder le Rental mis à jour
            Rental updatedRental = rentalService.updateRental(id, rental);

            // Retourner le Rental mis à jour
            return ResponseEntity.ok(updatedRental);

        } catch (Exception e) {
            // Log détaillé de l'exception pour le débogage
            logger.error("Erreur lors de la mise à jour du rental : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
