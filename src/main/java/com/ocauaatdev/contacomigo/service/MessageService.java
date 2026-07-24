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
import com.ocauaatdev.contacomigo.util.MessageIntentParser;
import com.ocauaatdev.contacomigo.util.SecurityUtils;
import com.ocauaatdev.contacomigo.util.TransactionFilter;
import com.ocauaatdev.contacomigo.util.TransactionParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
        userMessage = messageRepository.saveAndFlush(userMessage);

        // 3. Detecta a intenção e roteia para o método correto
        MessageIntent intent = MessageIntentParser.detect(dto.content());

        return switch (intent) {
            case REGISTER_TRANSACTION -> handleRegister(dto.content(), conversation, userMessage);
            case QUERY_EXTRACT        -> handleExtract(conversation, userMessage);
            case UNKNOWN              -> handleUnknown(conversation, userMessage);
        };
    }

    // ********* CASO 1: usuário quer registrar transação *********
    private MessageInteractionDTO handleRegister(String content, Conversation conversation, Message userMessage){
        TransactionParser.ParsedData parsed = TransactionParser.parse(content);

        if (parsed == null) {
            return handleUnknown(conversation, userMessage);
        }

        // Monta e salva a transação
        NewTransactionDTO newTransDTO = new NewTransactionDTO(
                parsed.description(),
                parsed.amount(),
                parsed.type(),
                parsed.category(),
                parsed.paymentMethod(),
                parsed.transactionDate(),
                conversation.getUser().getId(),
                conversation.getId()
        );

        ResponseTransactionDTO saved = transactionService.registerTransaction(newTransDTO);

        // Monta a resposta do assistente
        String tipo = parsed.type() == TypeTransaction.EXPENSE ? "despesa" : "receita";
        String responseText = String.format(
                "Registrado! Nova %s de R$ %s em '%s'.",
                tipo, parsed.amount(), parsed.description()
        );

        Message systemMessage = new Message(responseText, Sender.ASSISTANT, conversation);
        systemMessage = messageRepository.saveAndFlush(systemMessage);

        // Retorna com o transactionId para o frontend montar os botões de editar/deletar
        return new MessageInteractionDTO(
                new ResponseMessageDTO(userMessage),
                new ResponseMessageDTO(systemMessage),
                saved.id(),   // frontend usa esse ID nos botões
                null          // sem lista de transações nesse caso
        );
    }

    // ********* CASO 2: usuário quer ver o extrato — padrão 30 dias *********
    private MessageInteractionDTO handleExtract(
            Conversation conversation, Message userMessage) {

        // Filtro fixo: últimos 30 dias, sem outros filtros
        TransactionFilter filter = new TransactionFilter(
                LocalDate.now().minusDays(30),
                LocalDate.now(),
                null, null, null
        );

        List<ResponseTransactionDTO> transactions = transactionService.getAll(filter);

        String responseText = transactions.isEmpty()
                ? "Você não tem transações nos últimos 30 dias."
                : String.format("Aqui está seu extrato dos últimos 30 dias (%d transações):",
                transactions.size());

        Message systemMessage = new Message(responseText, Sender.ASSISTANT, conversation);
        systemMessage = messageRepository.saveAndFlush(systemMessage);

        // Retorna com a lista de transações para o frontend renderizar o card de extrato
        return new MessageInteractionDTO(
                new ResponseMessageDTO(userMessage),
                new ResponseMessageDTO(systemMessage),
                null,          // sem transactionId nesse caso
                transactions   // frontend usa essa lista para renderizar o extrato
        );
    }

    // ********* CASO 3: mensagem não reconhecida *********
    private MessageInteractionDTO handleUnknown(
            Conversation conversation, Message userMessage) {

        String responseText = "Não entendi esse comando. " +
                "Exemplos do que posso fazer: " +
                "'Gastei 50 uber', 'Ganhei 1500 salário', 'Ver meu extrato'.";

        Message systemMessage = new Message(responseText, Sender.ASSISTANT, conversation);
        systemMessage = messageRepository.saveAndFlush(systemMessage);

        return new MessageInteractionDTO(
                new ResponseMessageDTO(userMessage),
                new ResponseMessageDTO(systemMessage),
                null,
                null
        );
    }

    private void validateOwnership(Conversation conversation) {
        User authenticated = securityUtils.getAuthenticatedUser();
        if (!conversation.getUser().getId().equals(authenticated.getId())) {
            throw new ForbiddenException("You are not authorized to perform this action.");
        }
    }
}