package com.chatop.My_Chatop_Api.dto;

public class MessageRequest {

    private String message;  // Le contenu du message
    private Long userId;     // ID de l'utilisateur
    private Long rentalId;   // ID de la location

    // Getter et Setter pour message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter et Setter pour userId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter et Setter pour rentalId
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }
}
