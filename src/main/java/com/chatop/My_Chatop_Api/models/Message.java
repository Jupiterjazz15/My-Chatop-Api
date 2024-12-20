package com.chatop.My_Chatop_Api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @ManyToOne // Relation ManyToOne avec la location
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental; // Relation vers la location

    @NotNull
    @ManyToOne // Relation ManyToOne avec l'utilisateur
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Relation vers l'utilisateur

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Date de création du message

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Date de mise à jour du message


    // Constructeur par défaut (requis pour JPA)
    public Message() {}

    // Constructeur avec paramètres
    public Message(User user, Rental rental, String message) {
        this.user = user;
        this.rental = rental;
        this.message = message;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
