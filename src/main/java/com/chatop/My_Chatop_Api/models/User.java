package com.chatop.My_Chatop_Api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore; // annotation Jackson pr exclure certains champs lors de la sérialisation JSON (transfo en JSON)
import jakarta.persistence.*; // Fournit les annotations JPA pour gérer les interactions avec la DB @Entity, @Table, @Id
import jakarta.validation.constraints.Email; // Annotation @Email : format valide pour les adresses email
import jakarta.validation.constraints.NotBlank; // Annotation @NotBlank : champ obligatoire
import jakarta.validation.constraints.Size; // annotation @Size : taille minimale/maximale
import org.hibernate.annotations.CreationTimestamp; // annotation pr remplir automatiquement avec la date/heure de création de l'entité.
import org.hibernate.annotations.UpdateTimestamp;  // annotation pr mettre à jour automatiquement avec la date/heure de la dernière modification de l'entité.

import java.time.LocalDate;

@Entity
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String password;

    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate updatedAt;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

}
