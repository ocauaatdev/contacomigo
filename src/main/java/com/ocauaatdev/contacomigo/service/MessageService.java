package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.message.MessageInteractionDTO;
import com.ocauaatdev.contacomigo.dto.message.ResponseMessageDTO;
import com.ocauaatdev.contacomigo.dto.message.SendMessageDTO;
import com.ocauaatdev.contacomigo.dto.transaction.NewTransactionDTO;
import com.ocauaatdev.contacomigo.dto.transaction.ResponseTransactionDTO;
import com.ocauaatdev.contacomigo.entity.*;
import com.ocauaatdev.contacomigo.exception.ForbiddenException;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.ConversationRepository;
import com.ocauaatdev.contacomigo.repository.MessageRepository;
import com.ocauaatdev.contacomigo.util.SecurityUtils;
import com.ocauaatdev.contacomigo.util.TransactionParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SecurityUtils securityUtils;

    @Transactional
    public MessageInteractionDTO sendMessage(UUID conversationId, SendMessageDTO dto) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        validateOwnership(conversation);

        // 1. Salva a mensagem do usuário
        Message userMessage = new Message(dto.content(), Sender.USER, conversation);
        messageRepository.save(userMessage);

        // 2. Tenta extrair dados financeiros
        TransactionParser.ParsedData parsedData = TransactionParser.parse(dto.content());

        String systemResponseText;

        if (parsedData != null) {
            NewTransactionDTO newTransDTO = new NewTransactionDTO(
                    parsedData.description(),
                    parsedData.amount(),
                    parsedData.type(),
                    parsedData.category(),
                    parsedData.paymentMethod(),
                    parsedData.transactionDate(),
                    conversation.getUser().getId(),
                    conversation.getId()
            );

            ResponseTransactionDTO transDTO = transactionService.registerTransaction(newTransDTO);

//            Montando a resposta do sistema:
//            Se o type do parsed for EXPENSE(despesa) ele define como 'despesa', se não, define como 'receita'
            String tipo = parsedData.type() == TypeTransaction.EXPENSE ? "despesa" : "receita";

            systemResponseText = String.format("Registrado com sucesso! Nova %s de R$ %s em: '%s'.",
                    tipo, parsedData.amount(), parsedData.description());
        } else {
            systemResponseText = "Olá! Não entendi esse comando. Para registrar, use por exemplo: 'Gastei 35.90 uber' ou 'Ganhei 1500 salario'.";
        }

        // 3. Salva a resposta do sistema
        Message systemMessage = new Message(systemResponseText, Sender.ASSISTANT, conversation);
        messageRepository.save(systemMessage);

        // 4. Monta os DTOs individuais
        ResponseMessageDTO userDto = new ResponseMessageDTO(
                userMessage.getId(),
                userMessage.getContent(),
                userMessage.getSender(),
                userMessage.getCreatedAt()
        );

        ResponseMessageDTO systemDto = new ResponseMessageDTO(
                systemMessage.getId(),
                systemMessage.getContent(),
                systemMessage.getSender(),
                systemMessage.getCreatedAt()
        );

        // 5. Retorna tudo empacotado para o Front-end
        return new MessageInteractionDTO(userDto, systemDto);
    }

    private void validateOwnership(Conversation conversation) {
        User authenticated = securityUtils.getAuthenticatedUser();
        if (!conversation.getUser().getId().equals(authenticated.getId())) {
            throw new ForbiddenException("You are not authorized to perform this action.");
        }
    }
}