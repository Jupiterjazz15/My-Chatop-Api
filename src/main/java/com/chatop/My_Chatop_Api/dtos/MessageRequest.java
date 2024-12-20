package com.chatop.My_Chatop_Api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequest {

    @NotNull
    private Long rental_id;
    @NotNull
    private Long user_id;
    @NotBlank
    private String message;

    // Getters et setters
    public Long getRentalId() {
        return rental_id;
    }

    public void setRentalId(Long rental_id) {
        this.rental_id = rental_id;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
