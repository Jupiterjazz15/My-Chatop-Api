package com.chatop.My_Chatop_Api.controllers;

import com.chatop.My_Chatop_Api.dtos.MessageRequest;
import com.chatop.My_Chatop_Api.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/messages/")
public class MessageController {

    MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, String>> saveMessage(@Valid @RequestBody MessageRequest message){
        if(messageService.saveMessage(message) != null) {
            return ResponseEntity.ok(Map.of("message", "Message send with success"));
        }
        else {
            return ResponseEntity.ok(Map.of("error", "Message not send with success"));
        }

    }

}
