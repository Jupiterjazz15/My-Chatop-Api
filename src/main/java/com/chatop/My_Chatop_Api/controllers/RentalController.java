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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("api/rentals")
public class RentalController {

    RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createRental(    @RequestParam("name") String name,
                                                                @RequestParam("surface") BigDecimal surface,
                                                                @RequestParam("price") BigDecimal price,
                                                                @RequestParam("description") String description,
                                                                @RequestParam("picture") MultipartFile picture) throws IOException {

        RentalRequest rental = new RentalRequest();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setPicture(picture);

        if (rentalService.saveRental(rental)==null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rental already exists"));
        }
        else {
            return ResponseEntity.ok(Map.of("message", "Rental created!"));
        }

    }

    @GetMapping()
    public Map<String, List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        return Map.of("rentals", rentals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rental);
    }

    // put rental
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRental(@PathVariable Long id, @Valid @ModelAttribute  RentalRequest rental) throws IOException {
        try {
            Rental updatedRental = rentalService.updateRental(id, rental);
            if (updatedRental == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authorized or rental not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Rental updated!"));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }


}
