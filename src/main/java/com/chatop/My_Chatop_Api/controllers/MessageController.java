package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dto.MessageRequest;
import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.security.services.UserDetailsServicesImpl;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import com.chatop.My_Chatop_Api.security.jwt.JwtUtils;
import com.chatop.My_Chatop_Api.services.MessageService;
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

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class); // Déclaration du logger

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserDetailsServicesImpl userService;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @PostMapping("")
    public ResponseEntity<Message> createMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
        try {
        logger.debug("Creating message with rentalId: {} and message: {}", messageRequest.getRentalId(), messageRequest.getMessage());
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

            // Créer et sauvegarder le message
            Message message = messageService.createMessage(
                    messageRequest.getMessage(),
                    messageRequest.getRentalId(),
                    user
            );
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

