package com.ocauaatdev.contacomigo.service;

import com.ocauaatdev.contacomigo.dto.transaction.NewTransactionDTO;
import com.ocauaatdev.contacomigo.dto.transaction.ResponseTransactionDTO;
import com.ocauaatdev.contacomigo.dto.transaction.UpdateTransactionDTO;
import com.ocauaatdev.contacomigo.entity.Conversation;
import com.ocauaatdev.contacomigo.entity.Transaction;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.ForbiddenException;
import com.ocauaatdev.contacomigo.exception.ResourceNotFoundException;
import com.ocauaatdev.contacomigo.repository.ConversationRepository;
import com.ocauaatdev.contacomigo.repository.TransactionRepository;
import com.ocauaatdev.contacomigo.repository.UserRepository;
import com.ocauaatdev.contacomigo.util.SecurityUtils;
import com.ocauaatdev.contacomigo.util.TransactionFilter;
import com.ocauaatdev.contacomigo.util.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private SecurityUtils securityUtils;

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
        return new ResponseTransactionDTO(saved);

    }

    public ResponseTransactionDTO getTransactionById(UUID id) {
        Transaction transaction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found."));
        validateOwnership(transaction);

        return new ResponseTransactionDTO(transaction);
    }

    public ResponseTransactionDTO updateTransaction(UUID id, UpdateTransactionDTO dto){
        Transaction transaction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found."));
        validateOwnership(transaction);

        /*Maneira de ler Objects.requireNonNullElse:
        Insira a descrição do DTO, a menos que seja nula; se for nula, use a descrição atual da transação */
        transaction.setDescription(Objects.requireNonNullElse(dto.description(), transaction.getDescription()));
        transaction.setAmount(Objects.requireNonNullElse(dto.amount(), transaction.getAmount()));
        transaction.setCategory(Objects.requireNonNullElse(dto.category(), transaction.getCategory()));
        transaction.setPaymentMethod(Objects.requireNonNullElse(dto.paymentMethod(), transaction.getPaymentMethod()));
        transaction.setTransactionDate(Objects.requireNonNullElse(dto.transactionDate(), transaction.getTransactionDate()));

        Transaction updated = repository.save(transaction);
        return new ResponseTransactionDTO(updated);
    }

    public void deleteTransaction(UUID id){
        Transaction transaction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found."));
        validateOwnership(transaction);
        repository.delete(transaction);
    }

    //Este metodo faz o getAll de todas transações do usuario e aplica filtros caso o usuario utilize-os
    public List<ResponseTransactionDTO> getAll(TransactionFilter filter) {
        User authenticated = securityUtils.getAuthenticatedUser();

        Specification<Transaction> spec = Specification
                // byUser é SEMPRE aplicado — garante que o usuário só vê o que é dele
                .where(TransactionSpecification.byUser(authenticated.getId()))
                .and(TransactionSpecification.fromDate(filter.startDate()))
                .and(TransactionSpecification.toDate(filter.endDate()))
                .and(TransactionSpecification.byType(filter.type()))
                .and(TransactionSpecification.byCategory(filter.category()))
                .and(TransactionSpecification.byPaymentMethod(filter.paymentMethod()));

        return repository.findAll(spec).stream()
                .map(ResponseTransactionDTO::new) //É o equivalente compacto de .map(t -> new ResponseTransactionDTO(t))
                .sorted(Comparator.comparing(ResponseTransactionDTO::transactionDate).reversed()) //Ordena do mais recente para o mais antigo
                .toList();
    }

    private void validateOwnership(Transaction transaction) {
        User authenticated = securityUtils.getAuthenticatedUser();
        if (!transaction.getUser().getId().equals(authenticated.getId())) {
            throw new ForbiddenException("You are not authorized to perform this action.");
        }
    }
}
