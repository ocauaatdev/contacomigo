package com.ocauaatdev.contacomigo.repository;

import com.ocauaatdev.contacomigo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
