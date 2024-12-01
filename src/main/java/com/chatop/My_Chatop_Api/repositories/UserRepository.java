package com.chatop.My_Chatop_Api.repositories;

import org.springframework.stereotype.Repository; // annotation qui indique que cette interface est un composant Spring pour gérer l'accès aux données.
import org.springframework.data.jpa.repository.JpaRepository; // fournit l'interface JpaRepository incluant les mthds pr interagir avec la DB (CRUD, etc.).
import java.util.Optional; // utilisé pr éviter les retours null et indiquer qu'une valeur peut ou non être présente
import com.chatop.My_Chatop_Api.models.User; // import du model User

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // MTHD DE JPAREPOSITORY
    Optional<User> findByEmail(String email);

    // MTHD DE JPAREPOSITORY
    Optional<User>findById(long id);

    // MTHD DE JPAREPOSITORY POUR EVITER LES DOUBLONS
    Boolean existsByUsername(String username);

    // MTHD DE JPAREPOSITORY POUR EVITER LES DOUBLONS
    Boolean existsByEmail(String email);
}