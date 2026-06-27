package com.ocauaatdev.contacomigo.controller;

import com.ocauaatdev.contacomigo.dto.transaction.UpdateTransactionDTO;
import com.ocauaatdev.contacomigo.entity.Category;
import com.ocauaatdev.contacomigo.entity.PaymentMethod;
import com.ocauaatdev.contacomigo.entity.TypeTransaction;
import com.ocauaatdev.contacomigo.service.TransactionService;
import com.ocauaatdev.contacomigo.util.TransactionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransaction(@PathVariable UUID id){
        var result = service.getTransactionById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    /* EXEMPLOS:

    Sem filtro nenhum — retorna tudo do usuário:
    GET /transaction

    Só por tipo:
    GET /transaction?type=EXPENSE

    Por período e categoria:
    GET /transaction?startDate=2026-01-01&endDate=2026-06-30&category=FOOD

    Todos os filtros juntos:
    GET /transaction?startDate=2026-06-01&endDate=2026-06-30&type=EXPENSE&category=TRANSPORT
    */

    public ResponseEntity<Object> getAll(@RequestParam(required = false) LocalDate startDate,
                                         @RequestParam(required = false) LocalDate endDate,
                                         @RequestParam(required = false) TypeTransaction type,
                                         @RequestParam(required = false) Category category,
                                         @RequestParam(required = false)PaymentMethod paymentMethod){
        TransactionFilter filter = new TransactionFilter(startDate, endDate, category, type, paymentMethod);
        var result = service.getAll(filter);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTransaction(@PathVariable UUID id, @RequestBody UpdateTransactionDTO dto){
        var result = service.updateTransaction(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable UUID id){
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

}
