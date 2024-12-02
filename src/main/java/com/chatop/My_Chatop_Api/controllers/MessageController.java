package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dto.MessageRequest;
import com.chatop.My_Chatop_Api.models.Message;
import com.chatop.My_Chatop_Api.models.Rental;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.MessageRepository;
import com.chatop.My_Chatop_Api.repositories.RentalRepository;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import com.chatop.My_Chatop_Api.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthTokenFilter authTokenFilter;

    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);

    @PostMapping("")
    public ResponseEntity<Message> createMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
        try {
            // Extraire le JWT depuis la requête
            String jwt = this.authTokenFilter.parseJwt(request); // Méthode pour extraire le JWT du header "Authorization"
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Token invalide ou manquant
            }

            // Extraire l'email de l'utilisateur depuis le JWT et rechercher l'utilisateur dans la base
            String userEmail = jwtUtils.getUserNameFromJwtToken(jwt);
            Optional<User> user = userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Utilisateur non trouvé
            }

            // Récupérer la location associée à ce message
            Optional<Rental> rental = rentalRepository.findById(messageRequest.getRentalId());
            if (rental.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Location non trouvée
            }

            // Créer un nouvel objet Message et l'associer à l'utilisateur et à la location
            Message message = new Message();
            message.setMessage(messageRequest.getMessage());
            message.setUser(user.get());
            message.setRental(rental.get());

            // Sauvegarder le message dans la base de données
            messageRepository.save(message);

            // Retourner le message créé
            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (Exception e) {
            // Log détaillé de l'exception pour le débogage
            logger.error("Erreur lors de la création du message : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Gestion des erreurs internes
        }
    }
}
