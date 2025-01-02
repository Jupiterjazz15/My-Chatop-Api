package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dtos.User.LoginRequest;
import com.chatop.My_Chatop_Api.dtos.User.RegisterRequest;
import com.chatop.My_Chatop_Api.dtos.User.UserDto;
import com.chatop.My_Chatop_Api.services.JWTService;
import com.chatop.My_Chatop_Api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("api/auth")
public class LoginController {

    private JWTService jwtService;

    private UserService userService;

    public LoginController(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> getToken(@Valid @RequestBody LoginRequest loginRequest) {
        if (userService.existsByEmail(loginRequest.getEmail())) {
            String token = jwtService.generateToken( loginRequest.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "User does not exist"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Vérifier si l'utilisateur existe déjà
        if (userService.saveUser(registerRequest) == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }

        String token = jwtService.generateToken(registerRequest.getEmail());

        // Retourner le token JWT dans la réponse
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        // Récupérer l'objet Authentication depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build(); // Retourner une erreur 401 si non authentifié
        }

        // Extraire les détails de l'utilisateur
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaim("email");
        // Récupérer l'utilisateur à partir du service
        UserDto user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404).build(); // Retourner une erreur 404 si l'utilisateur n'est pas trouvé
        }

        return ResponseEntity.ok(user);
    }


}
