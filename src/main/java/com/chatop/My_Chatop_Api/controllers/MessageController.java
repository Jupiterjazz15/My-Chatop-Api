package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dtos.MessageRequest;
import com.chatop.My_Chatop_Api.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Envoyer un message")
    @PostMapping("/messages")
    public ResponseEntity<Map<String, String>> saveMessage(
            @Parameter(description = "Détails du message à envoyer") @Valid @RequestBody MessageRequest message) {
        if (messageService.saveMessage(message) != null) {
            return ResponseEntity.ok(Map.of("message", "Message sent successfully"));
        } else {
            return ResponseEntity.ok(Map.of("error", "Message not sent successfully"));
        }
    }
}
