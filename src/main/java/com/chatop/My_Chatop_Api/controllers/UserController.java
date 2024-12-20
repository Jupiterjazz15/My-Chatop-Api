package com.chatop.My_Chatop_Api.controllers;

import java.util.Optional;

import com.chatop.My_Chatop_Api.security.jwt.AuthTokenFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import com.chatop.My_Chatop_Api.dto.MessageResponse;
import com.chatop.My_Chatop_Api.dto.UserResponse;
import org.springframework.web.bind.annotation.CrossOrigin; // Prque les clients sur d'autres domaines puissent accéder à l'API (permissions des requêtes Cross-Origin Resource Sharing)
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;// Annotation utilisée pr définir le mapping des requêtes HTTP vers des méthodes spécifiques d'un contrôleur.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.chatop.My_Chatop_Api.security.jwt.JwtUtils;
import java.util.HashMap;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.chatop.My_Chatop_Api.dto.LoginRequest;
import org.springframework.security.core.Authentication;
import com.chatop.My_Chatop_Api.dto.JwtResponse;
import com.chatop.My_Chatop_Api.dto.SignupRequest;
import com.chatop.My_Chatop_Api.models.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Controller", description = "Endpoints for managing users")
@CrossOrigin(origins = "*", maxAge = 3600) // durée de mise en cache 1h
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired // injection de l'instance définit dans WebSecurityConfig
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthTokenFilter authTokenFilter;

    HashMap<String, Object> responseMessage = null; // Déclaration et initialisation de la var responseMessage, une HashMap utilisée pr stocker des paires clé [String]-valeur [Object].
    MappingJacksonValue jsonResponse = null; // objet MappingJacksonValue utilisé avec Jackson pr appliquer des règles de sérialisation spécifiques (ex. filtrage, vues) lors de la production d'une réponse JSON.


    public UserController() {  // CONSTRUCTEUR PAR DÉFAUT : Nécessaire pr des frameworks comme Jackson

        this.authTokenFilter = new AuthTokenFilter();
        this.responseMessage = new HashMap<String, Object>();
        // Création d'une nouvelle instance de HashMap pour la variable d'instance `responseMessage`.
        // Cela permet de stocker des paires clé-valeur pour préparer une réponse structurée.

        this.jsonResponse = new MappingJacksonValue(null);
        // Initialisation de la variable d'instance `jsonResponse` en créant une nouvelle instance de `MappingJacksonValue`.
        // La valeur passée au constructeur est `null`, ce qui signifie que l'objet JSON à mapper n'est pas encore défini.
    }

    // PERMETTRE UNE PREMIÈRE CONNEXION
    @Operation(
            summary = "Register a new user",
            description = "Allows a new user to register by providing email, username, and password. Returns a JWT token upon successful registration."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully and JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Bad request, email already in use or validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Renvoie une réponse HTTP personnalisée (ResponseEntity).
        // Elle prend en paramètre un objet `SignupRequest` (envoyé dans le corps de la requête).
        // `@Valid` : vérifie que les champs de `signUpRequest` respectent les contraintes définies (ex. : @NotBlank).
        // @RequestBody` : convertit le corps JSON de la requête en un objet Java.

        if (userRepository.existsByEmail(signUpRequest.getEmail())) { // Si l'email existe déjà, on renvoie une réponse d'erreur.
            return ResponseEntity
                    .badRequest()// Renvoie une réponse HTTP avec le statut 400 (bad request).
                    .body(new MessageResponse("Email is already used!", 400));// Renvoie un msg d'erreur dans le corps de la réponse.
        }

        // Crée une instance d'un utilisateur (nouveau user)
        User user = new User(signUpRequest.getUsername(), // Utilise les informations envoyées dans `signUpRequest`.
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()) // Encode le mot de passe avec `encoder` (par exemple, BCrypt).
        );
        userRepository.save(user); // Sauvegarde l'utilisateur dans la base de données.

        Authentication authentication = authenticationManager.authenticate( // Si l'email existe on authentifie le user avec l'email et le mot de passe.
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword())
        );// `authenticationManager.authenticate` : VERIFIE l'email et le mdp correspondent. Renvoie un objet `Authentication` contenant les infos du user authentifié.

        SecurityContextHolder.getContext().setAuthentication(authentication); // STOCKE les infos du user authentifié dans le `SecurityContextHolder, ce qui rend le user disponible pour le reste du traitement.

        String jwt = jwtUtils.generateJwtToken(authentication);// GENERE un token JWT pour le user authentifié à partir des info contenues dans l'objet `authentication`.

        // Création d'une instance `UserResponse` avec les données de l'utilisateur sauvegardé.
        UserResponse userResponse = new UserResponse(
                user.getId(), // ID généré pour l'utilisateur.
                user.getUsername(), // Nom d'utilisateur.
                user.getEmail(), // Email de l'utilisateur.
                user.getCreatedAt(), // Date de création de l'utilisateur.
                user.getUpdatedAt() // Date de la dernière mise à jour.
        );
        // Création d'une HashMap pour structurer la réponse contenant le token JWT et les infos utilisateur.
        HashMap<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwt);
        responseBody.put("user", userResponse);

        return ResponseEntity.ok(new JwtResponse(jwt)); // RETOURNE une réponse HTTP 200 (OK) avec un objet `JwtResponse` contenant le token JWT.
    }

    // PERMETTRE DE SE CONNECTER PAR LA SUITE
    @Operation(
            summary = "Authenticate a user",
            description = "Authenticate a user by validating their email and password. Returns a JWT token upon successful authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully and JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid email or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Renvoie une réponse HTTP personnalisée (ResponseEntity).
        // Prend en paramètre un objet `LoginRequest` (envoyé dans le corps de la requête).
        // `@Valid` : vérifie que les champs de `loginRequest` respectent les contraintes définies (ex. : @NotBlank).
        // `@RequestBody` : convertit le corps JSON de la requête en un objet Java.
        boolean isExist = this.userRepository.existsByEmail(loginRequest.getEmail()); // Mthd `existsByEmail` de `userRepository` pr vérifier si l'email existe.

        if (!isExist) {
            // Si l'email n'existe pas, on renvoie une réponse d'erreur.
            this.responseMessage.put("Error", "Email or Password incorrect!"); // Ajoute un message d'erreur dans la Map `responseMessage`.
            this.responseMessage.put("status", 401); // Ajoute un code HTTP 401 (non autorisé) dans `responseMessage`.
            this.jsonResponse.setValue(this.responseMessage); // Convertit la Map en JSON grâce à `MappingJacksonValue`.

            return new ResponseEntity<Object>(this.jsonResponse, HttpStatus.UNAUTHORIZED); // Retourne une réponse HTTP avec le JSON et le statut 401.
        }

        Authentication authentication = authenticationManager.authenticate( // Si l'email existe on authentifie le user avec l'email et le mot de passe.
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        // `authenticationManager.authenticate` : VERIFIE l'email et le mdp correspondent. Renvoie un objet `Authentication` contenant les infos du user authentifié.
        SecurityContextHolder.getContext().setAuthentication(authentication); // STOCKE les infos du user authentifié dans le `SecurityContextHolder, ce qui rend le user disponible pour le reste du traitement.
        String jwt = jwtUtils.generateJwtToken(authentication);// GENERE un token JWT pour le user authentifié à partir des info contenues dans l'objet `authentication`.
        return ResponseEntity.ok(new JwtResponse(jwt)); // RETOURNE une réponse HTTP 200 (OK) avec un objet `JwtResponse` contenant le token JWT.
    }


    // RECUPERER LES INFOS DU USER CONNECTE
    @Operation(
            summary = "Get current authenticated user info",
            description = "Retrieve the details of the currently authenticated user using their JWT token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request) {// Méthode qui renvoie un objet `UserResponse` contenant les détails du user connecté.

        Optional<User> user = Optional.empty(); // Variable `user` de type `Optional` cad un conteneur qui peut soit contenir un objet de type User, soit être vide
        UserResponse userResponse = null; // Variable `userResponse` pour stocker la réponse à renvoyer

        try {// Bloc de code entouré par un `try-catch` pour gérer les exceptions qui pourraient survenir.
            String jwt = this.authTokenFilter.parseJwt(request); // mthd  `parseJwt` du AuthTokenFilter pour extraire le JWT de la requête HTTP.
            String useremail = jwtUtils.getUserNameFromJwtToken(jwt); // Utilise le JWT pour récupérer l'email du user
            user = this.userRepository.findByEmail(useremail); // Recherche l'utilisateur dans la base de données à l'aide de son email

            userResponse = new UserResponse(
                    user.get().getId(),
                    user.get().getUsername(),
                    user.get().getEmail(),
                    user.get().getCreatedAt(),
                    user.get().getUpdatedAt()
            ); // Si le user est trouvé, on initialise un objet `UserResponse` avec les détails de l'utilisateur (mthds de User)

        } catch (Exception e) {
            // Gère les exceptions, par exemple, si le JWT est invalide ou si l'utilisateur n'est pas trouvé.
            // ATTENTION COCO
        }

        return userResponse;
    }
}

