package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import com.chatop.My_Chatop_Api.services.MessageService;
import com.chatop.My_Chatop_Api.security.services.UserDetailsServicesImpl;
import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/messages/")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);  // Logger pour la classe

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserDetailsServicesImpl userService;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<Message> createMessage(
            @RequestParam(value = "rental_id") Long rental_id,
            @RequestParam(value = "user_id") Long user_id,
            @RequestParam(value = "message") String messageContent,
            HttpServletRequest request) {

        System.out.println(" !! RENTAL ID !! " + rental_id + "!! USER ID !!" + user_id);

/*
        try {
            logger.debug("Creating message with rentalId: {}, userId: {}, and message: {}",
                    rentalId, userId, messageContent);

            // Vérifie si rentalId et userId sont valides
            if (rentalId == null || userId == null || messageContent == null || messageContent.isEmpty()) {
                logger.error("Invalid input parameters. rentalId: {}, userId: {}, messageContent: {}", rentalId, userId, messageContent);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Reponse 400 si ID ou message manquant
            }

            // Récupérer le JWT depuis la requête
            String jwt = authTokenFilter.parseJwt(request);
            if (jwt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Récupérer l'utilisateur à partir du JWT
            User user = userService.getUserFromJwt(jwt);
            if (user == null) {
                logger.error("User not found from JWT");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Log pour vérifier la récupération des entités Rental et User
            logger.debug("Found user: {}", user);

            Rental rental = rentalRepository.findByOwnerId(userId)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Rental not found with owner id: " + userId));

            logger.debug("Found rental: {}", rental);

            // Créer et sauvegarder le message
            Message savedMessage = messageService.createMessage(rentalId, user.getId(), messageContent);

            logger.debug("Message created successfully: {}", savedMessage);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);

        } catch (RuntimeException e) {
            // Log l'erreur spécifique liée à la RuntimeException
            logger.error("RuntimeException occurred while creating message: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Rental ou User non trouvé
        } catch (Exception e) {
            // Log l'erreur générale
            logger.error("Unexpected error occurred while creating message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur interne serveur
        }
    } */

        return null;
    }

}
