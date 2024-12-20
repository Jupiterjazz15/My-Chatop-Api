package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dto.MessageRequest;
import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import com.chatop.My_Chatop_Api.services.MessageService;
import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import com.chatop.My_Chatop_Api.security.jwt.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.security.core.Authentication;

@Tag(name = "Message Controller", description = "Endpoints for managing messages")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private AuthTokenFilter authTokenFilter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private JwtUtils jwtUtils;

    // ENVOYER UN MESSAGE
    @Operation(
            summary = "Create a new message",
            description = "Creates a new message associated with a specific rental. Requires the rental ID and the message content."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Rental or User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping("")
    public ResponseEntity<?> createMessage(
            @RequestParam("rental_id") Long rentalId,
            @RequestParam("user_id") Long userId,
            @RequestParam("message") String message,
            HttpServletRequest request) {

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

            // Récupérer le rental associé
            Optional<Rental> rental = rentalRepository.findById(rentalId);
            if (rental.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental not found.");
            }

            // Créer un token d'authentification basé sur l'utilisateur récupéré
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.get(),  // L'utilisateur récupéré
                    null      // Pas besoin de mot de passe ici, car on utilise le JWT pour l'authentification
            );

            // Enregistrer l'authentification dans le SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Créer le message avec les informations extraites
            Message newMessage = new Message();
            newMessage.setRental(rental.get());  // Associer le rental à ce message
            newMessage.setUser(user.get());  // Associer l'utilisateur à ce message
            newMessage.setMessage(message);  // Le contenu du message

            // Sauvegarder le message
            Message createdMessage = messageService.createMessage(newMessage);

            // Retourner le message créé
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
