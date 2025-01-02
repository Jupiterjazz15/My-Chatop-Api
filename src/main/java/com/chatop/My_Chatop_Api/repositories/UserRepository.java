package com.chatop.My_Chatop_Api.repositories;

import org.springframework.stereotype.Repository; // annotation qui indique que cette interface est un composant Spring pour gérer l'accès aux données.
import org.springframework.data.jpa.repository.JpaRepository; // fournit l'interface JpaRepository incluant les mthds pr interagir avec la DB (CRUD, etc.).
import com.chatop.My_Chatop_Api.models.User; // import du model User

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // MTHD DE JPAREPOSITORY POUR EVITER LES DOUBLONS
    boolean existsByEmail(String email);

    // MTHD DE JPAREPOSITORY
    User findByEmail(String email);

}