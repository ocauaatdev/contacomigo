package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.transaction.NewTransactionDTO;
import com.ocauaatdev.contacomigo.dto.transaction.ResponseTransactionDTO;
import com.ocauaatdev.contacomigo.entity.Transaction;
import com.ocauaatdev.contacomigo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    public ResponseTransactionDTO registerTransaction(NewTransactionDTO dto){

        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setCategory(dto.category());
        transaction.setPaymentMethod(dto.paymentMethod());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setUser(dto.user());
        transaction.setConversation(dto.conversation());

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
