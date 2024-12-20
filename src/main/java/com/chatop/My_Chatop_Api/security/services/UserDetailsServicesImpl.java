package com.chatop.My_Chatop_Api.security.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service; // annotation indiquant que ce composant Spring est de type service
import org.springframework.security.core.userdetails.UserDetailsService; // Interface de SSECU utilisée pr récupérer les infos d'un user pr l'authentification
import com.chatop.My_Chatop_Api.repositories.UserRepository; // interface dont le but est de fournir les opérations nécessaires pour interagir avec DB
import org.springframework.beans.factory.annotation.Autowired; // annotation nécessaire à l'injection de dépendance
import org.springframework.security.core.userdetails.UserDetails; // interface de SSECU pr représenter les infos d'un user
import com.chatop.My_Chatop_Api.models.User; // ma classe réprésentant les caractéristiques et les données liées à un user
import org.springframework.security.core.userdetails.UsernameNotFoundException; // une classe héritant de RuntimeException de SSECU utilisée pr signaler qu'un user n'a pas été trouvé lors de la tentative de récupération de ses infos.
import com.chatop.My_Chatop_Api.security.jwt.JwtUtils; // Importation de JwtUtils pour gérer l'extraction de l'email

@Service // OVERRIDE DE LA MTHD LOADBYUSERNAME DE SSECU POUR FAIRE UNE RECHERCHE PAR MAIL
public class UserDetailsServicesImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;  // Injection du UserRepository

    @Autowired
    private JwtUtils jwtUtils; // Injection de JwtUtils pour gérer le JWT

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche de l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        return UserDetailsImpl.build(user); // Construction et retour de l'instance de UserDetailsImpl
    }

    // Méthode pour récupérer l'utilisateur à partir du JWT
    public User getUserFromJwt(String jwt) {
        // Extraire l'email à partir du JWT
        String email = jwtUtils.getUserNameFromJwtToken(jwt);
        // Recherche de l'utilisateur par email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Nouvelle méthode pour récupérer l'utilisateur connecté
    public User getAuthenticatedUser() {
        // Récupérer le JWT depuis le contexte d'authentification
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        // Extraire le token depuis le contexte
        String jwt = (String) authentication.getCredentials();
        if (jwt == null) {
            throw new RuntimeException("JWT token is missing");
        }

        // Extraire l'email depuis le JWT
        String email = jwtUtils.getUserNameFromJwtToken(jwt);

        // Rechercher l'utilisateur dans la base de données
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

}
