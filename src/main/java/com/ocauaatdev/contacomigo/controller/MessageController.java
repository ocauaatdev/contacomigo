package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.message.MessageInteractionDTO;
import com.ocauaatdev.contacomigo.dto.message.ResponseMessageDTO;
import com.ocauaatdev.contacomigo.dto.message.SendMessageDTO;
import com.ocauaatdev.contacomigo.dto.message.UpdateMessageDTO;
import com.ocauaatdev.contacomigo.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping
    public ResponseEntity<Page<ResponseMessageDTO>> getAllMessagesByConversation(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(messageService.getAllMessages(conversationId, page, size));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<ResponseMessageDTO> getMessageById(@PathVariable UUID conversationId, @PathVariable UUID messageId){
        var result = messageService.getMessage(conversationId, messageId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Object> deleteMessageById(@PathVariable UUID conversationId, @PathVariable UUID messageId){
        messageService.deleteMessage(conversationId, messageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<ResponseMessageDTO> updateMessage(
            @PathVariable UUID conversationId,
            @PathVariable UUID messageId,
            @RequestBody @Valid UpdateMessageDTO dto) {
        var result = messageService.updateMessage(conversationId, messageId, dto);
        return ResponseEntity.ok(result);
    }
}
