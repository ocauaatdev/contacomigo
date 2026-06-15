package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.user.UserCreateDTO;
import com.ocauaatdev.contacomigo.dto.user.UserResponseDTO;
import com.ocauaatdev.contacomigo.dto.user.UserUpdateDTO;
import com.ocauaatdev.contacomigo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserCreateDTO dto){
        UserResponseDTO result = service.create(dto);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO dto){
        UserResponseDTO result = service.update(id, dto);
        return ResponseEntity.ok().body(result);
    }
}
