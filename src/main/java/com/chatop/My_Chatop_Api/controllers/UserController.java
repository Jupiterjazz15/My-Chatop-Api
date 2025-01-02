package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dtos.User.UserDto;
import com.chatop.My_Chatop_Api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Récupérer un utilisateur par son ID")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "ID de l'utilisateur à récupérer") @PathVariable Long id) {
        UserDto user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
