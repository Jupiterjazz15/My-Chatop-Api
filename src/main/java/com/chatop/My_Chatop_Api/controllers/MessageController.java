package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dto.MessageRequest;
import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import com.chatop.My_Chatop_Api.services.MessageService;
import com.chatop.My_Chatop_Api.security.services.UserDetailsServicesImpl;
import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
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

    // Ajout des repositories
    @Autowired
    private RentalRepository rentalRepository;  // Injection de RentalRepository
    @Autowired
    private UserRepository userRepository;  // Injection de UserRepository

    @PostMapping("")
    public ResponseEntity<Message> createMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
        try {
            logger.debug("Creating message with rentalId: {}, userId: {}, and message: {}",
                    messageRequest.getRentalId(), messageRequest.getUserId(), messageRequest.getMessage());

            // Vérifie si rentalId et userId sont valides avant d'essayer d'enregistrer le message
            if (messageRequest.getRentalId() == null || messageRequest.getUserId() == null) {
                logger.error("rentalId or userId is null. rentalId: {}, userId: {}", messageRequest.getRentalId(), messageRequest.getUserId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Reponse 400 si ID manquant
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

            Rental rental = rentalRepository.findById(messageRequest.getRentalId())
                    .orElseThrow(() -> new RuntimeException("Rental not found with id: " + messageRequest.getRentalId()));

            logger.debug("Found rental: {}", rental);

            // Créer et sauvegarder le message
            Message message = new Message();
            message.setMessage(messageRequest.getMessage());
            message.setUserId(user.getId());  // Set the userId
            message.setRentalId(rental.getId());  // Set the rentalId

            messageService.createMessage(messageRequest.getMessage(), messageRequest.getRentalId(), user.getId());

            logger.debug("Message created successfully: {}", message);

            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (RuntimeException e) {
            // Log l'erreur spécifique liée à la RuntimeException
            logger.error("RuntimeException occurred while creating message: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Utilisateur non trouvé
        } catch (Exception e) {
            // Log l'erreur générale
            logger.error("Unexpected error occurred while creating message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur interne serveur
        }
    }
}
