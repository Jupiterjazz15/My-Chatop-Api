package com.chatop.My_Chatop_Api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity // cette classe est une entitée JPA = elle sera mappée à une table
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // génération automatique
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false) // Doit être supérieure à 0
    @Column(nullable = false)
    private BigDecimal surface;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    private BigDecimal price;

    @Size(max = 255)
    @Column(length = 255)
    private String picture;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long owner_id;

    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate created_at;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate updated_at;;

    // CONSTRUCTEUR PAR DEFAUT (requis pour JPA)
    public Rental() {}

    // CONSTRUCTEUR AVEC PARAMETRES
    public Rental(String name, BigDecimal surface, BigDecimal price, String picture, String description, Long owner_id) {
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.picture = picture;
        this.description = description;
        this.owner_id = owner_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSurface() {
        return surface;
    }

    public void setSurface(BigDecimal surface) {
        this.surface = surface;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(Long owner_id) {
        this.owner_id = owner_id;
    }

    public LocalDate getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.created_at = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updated_at = updatedAt;
    }

}
