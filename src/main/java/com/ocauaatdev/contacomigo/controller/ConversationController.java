package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.conversation.NewconversationDTO;
import com.ocauaatdev.contacomigo.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationService service;

    @PostMapping("/new")
    public ResponseEntity<Object> newConversation(@RequestBody NewconversationDTO dto){
        var result = service.newConversation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
