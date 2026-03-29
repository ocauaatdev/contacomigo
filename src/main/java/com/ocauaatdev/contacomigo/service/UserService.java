package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.user.UserCreateDTO;
import com.ocauaatdev.contacomigo.dto.user.UserCreateResponseDTO;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.BusinessException;
import com.ocauaatdev.contacomigo.exception.DataAlreadyExistsException;
import com.ocauaatdev.contacomigo.exception.PasswordFormatException;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserCreateResponseDTO create(UserCreateDTO dto){

        if (dto.name() == null || dto.name().isBlank()){
            throw new BusinessException("User name cannot be empty");
        }

        if (dto.email() == null || dto.email().isBlank()){
            throw new BusinessException("User email cannot be empty");
        }
        else if (repository.findByEmailIgnoreCase(dto.email()).isPresent()){
            throw new DataAlreadyExistsException("This email already exists.");
        }

        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{7,}$";

        if (dto.password() == null || !dto.password().matches(regexPassword)) {
            throw new PasswordFormatException();
        }

        if (dto.balance() == null){
            throw new BusinessException("User balance cannot be empty.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(encryptedPassword);
        user.setBalance(dto.balance());

        repository.save(user);

        return new UserCreateResponseDTO(user.getName(), user.getEmail(), user.getBalance());
    }
}
