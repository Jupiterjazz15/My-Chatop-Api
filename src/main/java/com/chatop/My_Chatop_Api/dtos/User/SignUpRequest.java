package com.chatop.My_Chatop_Api.dtos.User;

import jakarta.validation.constraints.*;// Import des annotations pour la validation des champs.

// DTO POUR TRANSPORTER LES DONNES D'INSCRIPTION ENVOYÉES PAR UN USER (PREMIERE CONNEXION)
public class SignUpRequest {
    // ATTRIBUTS
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    // GETTERS & SETTERS
    public String getName() {
        return name;
    }

    public void setnName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
