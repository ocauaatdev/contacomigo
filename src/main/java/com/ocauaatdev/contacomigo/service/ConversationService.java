package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.conversation.NewconversationDTO;
import com.ocauaatdev.contacomigo.dto.conversation.ResponseConversationDTO;
import com.ocauaatdev.contacomigo.entity.Conversation;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.ConversationRepository;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository repository;

    @Autowired
    private UserRepository userRepository;

    public ResponseConversationDTO newConversation(NewconversationDTO dto){

        User user = userRepository.findById(dto.idUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String finalTitle = dto.title();

        if (finalTitle == null || finalTitle.isBlank()){
            finalTitle = "Chat " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        Conversation conversation = new Conversation();
        conversation.setTitle(finalTitle);
        conversation.setUser(user);

        repository.save(conversation);
        return new ResponseConversationDTO(conversation.getId(), conversation.getTitle(), conversation.getCreatedAt());
    }
}
