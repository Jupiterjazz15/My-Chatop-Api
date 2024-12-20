package com.chatop.My_Chatop_Api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "rental_id")
    private Long rental_id;

    @JoinColumn(name = "user_id")
    private Long user_id;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt; // Date de création du message

    @Column(name = "updated_at")
    private LocalDate updatedAt; // Date de mise à jour du message


    // Constructeur par défaut (requis pour JPA)
    public Message() {}

    // Constructeur avec paramètres
    public Message(Long rental_id, Long user_id, String message) {
        this.rental_id = rental_id;
        this.user_id = user_id;
        this.message = message;

    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public Long getRentalId() {
        return rental_id;
    }
    public void setRentalId(Long rental_id) {
        this.rental_id = rental_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
