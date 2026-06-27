package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.message.MessageInteractionDTO;
import com.ocauaatdev.contacomigo.dto.message.SendMessageDTO;
import com.ocauaatdev.contacomigo.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/conversation/{conversationId}/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageInteractionDTO> sendMessage(
            @PathVariable UUID conversationId,
            @RequestBody @Valid SendMessageDTO dto){
        MessageInteractionDTO interaction = messageService.sendMessage(conversationId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(interaction);
    }
}
