package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.conversation.NewconversationDTO;
import com.ocauaatdev.contacomigo.dto.conversation.ResponseConversationDTO;
import com.ocauaatdev.contacomigo.dto.conversation.ResponseUpdateConversationDTO;
import com.ocauaatdev.contacomigo.entity.Conversation;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.ForbiddenException;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.ConversationRepository;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import com.ocauaatdev.contacomigo.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public ResponseConversationDTO newConversation(NewconversationDTO dto){

        User user = securityUtils.getAuthenticatedUser();

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

    public ResponseConversationDTO getConversation(UUID id){
        Conversation conversation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        validateOwnership(conversation);

        return new ResponseConversationDTO(conversation.getId(), conversation.getTitle(), conversation.getCreatedAt());
    }

    public List<ResponseConversationDTO> getConversations(){
        User authenticatedUser = securityUtils.getAuthenticatedUser();
        List<Conversation> conversations = repository.findByUserId(authenticatedUser.getId());

        return conversations.stream()
                .map(conversation -> new ResponseConversationDTO(conversation.getId(), conversation.getTitle(), conversation.getCreatedAt()))
                .toList();
    }

    public ResponseUpdateConversationDTO updateConversation(UUID idConversation, NewconversationDTO dto){
        Conversation conversation = repository.findById(idConversation)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        validateOwnership(conversation);

        String finalTitle = dto.title();
        if (finalTitle == null || finalTitle.isBlank()){
            finalTitle = "Chat " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        conversation.setTitle(finalTitle);
        repository.save(conversation);

        return new ResponseUpdateConversationDTO(conversation.getId(), conversation.getTitle(), conversation.getCreatedAt(), conversation.getUpdatedAt());
    }

    public void deleteConversation(UUID id){
        Conversation conversation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        validateOwnership(conversation);

        repository.deleteById(id);
    }

    private void validateOwnership(Conversation conversation){
        User authenticatedUser = securityUtils.getAuthenticatedUser();
        if (!conversation.getUser().getId().equals(authenticatedUser.getId())) {
            throw new ForbiddenException("You are not authorized to perform this action.");
        }
    }
}
