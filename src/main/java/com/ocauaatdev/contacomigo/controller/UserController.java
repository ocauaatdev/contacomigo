package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.user.UpdatePasswordDTO;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @RequestBody @Valid UpdatePasswordDTO dto){
        service.updatePassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable UUID id) {
        UserResponseDTO result = service.getById(id);
        return ResponseEntity.ok().body(result);
    }
}
