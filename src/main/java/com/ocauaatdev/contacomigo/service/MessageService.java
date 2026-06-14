package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.message.MessageInteractionDTO;
import com.ocauaatdev.contacomigo.dto.message.ResponseMessageDTO;
import com.ocauaatdev.contacomigo.dto.message.SendMessageDTO;
import com.ocauaatdev.contacomigo.dto.transaction.NewTransactionDTO;
import com.ocauaatdev.contacomigo.dto.transaction.ResponseTransactionDTO;
import com.ocauaatdev.contacomigo.entity.Conversation;
import com.ocauaatdev.contacomigo.entity.Message;
import com.ocauaatdev.contacomigo.entity.Sender;
import com.ocauaatdev.contacomigo.entity.TypeTransaction;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.ConversationRepository;
import com.ocauaatdev.contacomigo.repository.MessageRepository;
import com.ocauaatdev.contacomigo.util.TransactionParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    public MessageInteractionDTO sendMessage(SendMessageDTO dto) {

        Conversation conversation = conversationRepository.findById(dto.conversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        // 1. Salva a mensagem do usuário
        Message userMessage = new Message(dto.content(), Sender.USER, conversation, LocalDateTime.now());
        messageRepository.save(userMessage);

        // 2. Tenta extrair dados financeiros
        TransactionParser.ParsedData parsedData = TransactionParser.parse(dto.content());

        String systemResponseText;

        if (parsedData != null) {
            // Removido o conversation.getUser() que havia voltado para cá
            NewTransactionDTO newTransDTO = new NewTransactionDTO(
                    parsedData.description(),
                    parsedData.amount(),
                    parsedData.type(),
                    null,
                    parsedData.paymentMethod(),
                    parsedData.transactionDate(),
                    conversation.getUser(),
                    conversation
            );

            ResponseTransactionDTO transDTO = transactionService.registerTransaction(newTransDTO);

            String tipo = parsedData.type() == TypeTransaction.EXPENSE ? "despesa" : "receita";
            systemResponseText = String.format("Registrado com sucesso! Nova %s de R$ %s em: '%s'.",
                    tipo, parsedData.amount(), parsedData.description());
        } else {
            systemResponseText = "Olá! Não entendi esse comando. Para registrar, use por exemplo: 'Gastei 35.90 uber' ou 'Ganhei 1500 salario'.";
        }

        // 3. Salva a resposta do sistema
        Message systemMessage = new Message(systemResponseText, Sender.ASSISTANT, conversation, LocalDateTime.now());
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
}