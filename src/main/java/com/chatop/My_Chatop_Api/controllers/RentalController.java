package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dtos.RentalRequest;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.services.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
@RequestMapping("api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Créer une nouvelle location")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createRental(
            @Parameter(description = "Nom de la location") @RequestParam("name") String name,
            @Parameter(description = "Surface de la location") @RequestParam("surface") Double surface,
            @Parameter(description = "Prix de la location") @RequestParam("price") Double price,
            @Parameter(description = "Description de la location") @RequestParam("description") String description,
            @Parameter(description = "Photo de la location") @RequestParam("picture") MultipartFile picture) throws IOException {

        RentalRequest rental = new RentalRequest();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setPicture(picture);

        if (rentalService.saveRental(rental) == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rental already exists"));
        } else {
            return ResponseEntity.ok(Map.of("message", "Rental created!"));
        }
    }

    @Operation(summary = "Récupérer la liste de toutes les locations")
    @GetMapping()
    public Map<String, List<Rental>> getRentals() {
        List<Rental> rentals = rentalService.getRentals();
        return Map.of("rentals", rentals);
    }

    @Operation(summary = "Récupérer une location par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(
            @Parameter(description = "ID de la location à récupérer") @PathVariable Long id) {
        Rental rental = rentalService.getRental(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rental);
    }

    @Operation(summary = "Mettre à jour une location existante")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRental(
            @Parameter(description = "ID de la location à mettre à jour") @PathVariable Long id,
            @Parameter(description = "Détails de la location à mettre à jour") @Valid @ModelAttribute RentalRequest rental) throws IOException {
        try {
            Rental updatedRental = rentalService.updateRental(id, rental);
            // Gérer le cas où la location est introuvable
            if (updatedRental == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Rental not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Rental updated!"));

            // Gérer le cas où l'utilisateur n'est pas autorisé
        } catch (AccessDeniedException ex) {
            // Gérer le cas où l'utilisateur n'est pas autorisé
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }
}
