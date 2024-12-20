package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dto.RentalRequest;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Rental Controller", description = "Endpoints for managing rentals")
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

    // RECUPERER TOUTES LES LOCATIONS

    @Operation(summary = "Get all rentals", description = "Returns a list of all rentals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of rentals returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("")
    public ResponseEntity<List<Rental>> getAllRentals() {

        List<Rental> rentals = rentalService.getAllRentals();

        if (!rentals.isEmpty()) {
            return ResponseEntity.ok(rentals); // Retourne un statut 200 avec la liste des locations
        } else {
            Rental rentalNotFound = new Rental();
            rentalNotFound.setName("No rentals found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(rentalNotFound)); // Retourne un 404 avec un message d'erreur
        }
    }

    // RECUPERER UNE LOCATION VIA SON ID

    @Operation(
            summary = "Get a rental by ID",
            description = "Retrieve a specific rental by its unique ID. Returns 404 if the rental is not found."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rental retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Rental not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")

    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        Optional<Rental> rental = rentalService.getRentalById(id);

        if (rental.isPresent()) {
            return ResponseEntity.ok(rental.get()); // Retourne l'objet Rental si trouvé
        } else {
            return ResponseEntity.notFound().build(); // Retourne un 404 si le Rental n'existe pas
        }
    }


    // CREER UN RENTAL

    @Operation(
            summary = "Create a new rental",
            description = "Create a new rental associated with the authenticated user. The picture parameter is optional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rental created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping("")

    public ResponseEntity<?> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            HttpServletRequest request) {

        try {
            // Log de tous les en-têtes pour vérifier si Authorization est présent
            Enumeration<String> headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String headerName = headers.nextElement();
                System.out.println("Header: " + headerName + " = " + request.getHeader(headerName));
            }

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

            // Utiliser l'utilisateur trouvé comme le propriétaire du rental
            User owner = user.get();

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
            rental.setOwner(owner); // Associer le rental à l'utilisateur authentifié

            // Sauvegarder le Rental
            Rental createdRental = rentalService.createRental(rental);

            // Retourner le Rental créé
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);

        } catch (Exception e) {
            // Log détaillé de l'exception pour le débogage
            System.out.println("Erreur lors de la création du rental : ");
            e.printStackTrace(); // Pour afficher l'exception détaillée dans la console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }


    // MAJ UN RENTAL

    @Operation(
            summary = "Update an existing rental",
            description = "Update the details of a rental associated with the authenticated user. All parameters are optional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rental updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden, you can only update your own rentals"),
            @ApiResponse(responseCode = "404", description = "Rental or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PutMapping("/{id}")

    public ResponseEntity<?> updateRental(
            @PathVariable Long id,
            @ModelAttribute RentalRequest rentalRequest,  // Utilisation de ModelAttribute pour récupérer les données du formulaire
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
            if (!rental.getOwner().getId().equals(user.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own rentals!");
            }

            // Mettre à jour les champs du Rental si des valeurs sont fournies
            if (rentalRequest.getName() != null) rental.setName(rentalRequest.getName());
            if (rentalRequest.getSurface() != null) rental.setSurface(rentalRequest.getSurface());
            if (rentalRequest.getPrice() != null) rental.setPrice(rentalRequest.getPrice());
            if (rentalRequest.getDescription() != null) rental.setDescription(rentalRequest.getDescription());

            // Sauvegarder l'image si présente
            String picturePath = null;
            if (rentalRequest.getPicture() != null && !rentalRequest.getPicture().isEmpty()) {
                picturePath = fileStorageService.saveFile(rentalRequest.getPicture());
                rental.setPicture(picturePath);
            }

            // Sauvegarder le Rental mis à jour
            Rental updatedRental = rentalService.updateRental(id, rental);

            // Retourner le Rental mis à jour
            return ResponseEntity.ok(updatedRental);

        } catch (Exception e) {
            // Log détaillé de l'exception pour le débogage
            System.out.println("Erreur lors de la mise à jour du rental : ");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
