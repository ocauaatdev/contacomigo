package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.conversation.NewconversationDTO;
import com.ocauaatdev.contacomigo.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationService service;

    @PostMapping("/new")
    public ResponseEntity<Object> newConversation(@RequestBody @Valid NewconversationDTO dto){
        var result = service.newConversation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getConversation(@PathVariable UUID id){
        var result = service.getConversation(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Object> getAllConversations(){
        var result = service.getConversations();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateConversation(@PathVariable UUID id, @RequestBody @Valid NewconversationDTO dto){
        var result = service.updateConversation(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteConversation(@PathVariable UUID id){
        service.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }
}
