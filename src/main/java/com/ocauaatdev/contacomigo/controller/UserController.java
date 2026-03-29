package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.user.UserCreateDTO;
import com.ocauaatdev.contacomigo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserCreateDTO dto){
        var result = service.create(dto);
        return ResponseEntity.ok().body(result);
    }
}
