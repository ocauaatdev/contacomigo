package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.user.UserCreateDTO;
import com.ocauaatdev.contacomigo.dto.user.UserResponseDTO;
import com.ocauaatdev.contacomigo.dto.user.UserUpdateDTO;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.BusinessException;
import com.ocauaatdev.contacomigo.exception.DataAlreadyExistsException;
import com.ocauaatdev.contacomigo.exception.PasswordFormatException;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponseDTO create(UserCreateDTO dto){

        validateUserInfos(dto.name(), dto.email());

        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{7,}$";

        if (dto.password() == null || !dto.password().matches(regexPassword)) {
            throw new PasswordFormatException();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(encryptedPassword);

        repository.save(user);

        return new UserResponseDTO(user.getName(), user.getEmail());
    }

    public UserResponseDTO update(UUID id, UserUpdateDTO dto){

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        validateUserInfos(dto.name(), dto.email());

        user.setName(dto.name());
        user.setEmail(dto.email());

        repository.save(user);

        return new UserResponseDTO(user.getName(), user.getEmail());
    }

    private void validateUserInfos(String name, String email){
        if (name == null || name.isBlank()){
            throw new BusinessException("User name cannot be empty");
        }

        if (email == null || email.isBlank()){
            throw new BusinessException("User email cannot be empty");
        }
        else if (repository.findByEmailIgnoreCase(email).isPresent()){
            throw new DataAlreadyExistsException("This email already exists.");
        }
    }
}
