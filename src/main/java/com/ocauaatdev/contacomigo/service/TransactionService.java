package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.transaction.NewTransactionDTO;
import com.ocauaatdev.contacomigo.dto.transaction.ResponseTransactionDTO;
import com.ocauaatdev.contacomigo.entity.Conversation;
import com.ocauaatdev.contacomigo.entity.Transaction;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.ConversationRepository;
import com.ocauaatdev.contacomigo.repository.TransactionRepository;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    public ResponseTransactionDTO registerTransaction(NewTransactionDTO dto){

        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setCategory(dto.category());
        transaction.setPaymentMethod(dto.paymentMethod());
        transaction.setTransactionDate(dto.transactionDate());

        User user = userRepository.findById(dto.idUser()).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Conversation conversation = conversationRepository.findById(dto.idConversation()).orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        transaction.setUser(user);
        transaction.setConversation(conversation);

        Transaction saved = repository.save(transaction);

        return new ResponseTransactionDTO(
                saved.getId(),
                saved.getDescription(),
                saved.getAmount(),
                saved.getType(),
                saved.getCategory(),
                saved.getPaymentMethod(),
                saved.getTransactionDate()
        );

    }
}
