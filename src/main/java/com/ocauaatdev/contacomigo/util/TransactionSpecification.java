package com.ocauaatdev.contacomigo.util;

import com.ocauaatdev.contacomigo.entity.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class TransactionSpecification {

    //esse metodo vai filtrar as transações pelo usuario
    public static Specification<Transaction> byUser(UUID userId) {
        return (root, query, cb) ->
        cb.equal(root.get("user").get("id"), userId);
    }

    //esse metodo vai filtrar as datas que são maiores ou iguais a data passada no parâmetro (startDate)
    public static Specification<Transaction> fromDate(LocalDate startDate){
        return (root, query, cb) -> {
            if (startDate == null){
                return cb.conjunction(); // .conjunction garante um retorno não nulo, ele retorna 1=1.
                // esse filtro não remove nenhuma transação da busca.
                // Além disso, os bancos de dados modernos (PostgreSQL, MySQL, etc.)
                // são inteligentes o suficiente para otimizar a query e simplesmente apagar esse 1=1 antes de executar a busca
            }
          return cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate);
        };
    }

    //esse metodo vai filtrar as datas que são menores ou iguais a data passada no parâmetro (endDate)
    public static Specification<Transaction> toDate(LocalDate endDate){
        return (root, query, cb) ->{
            if (endDate == null){
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("transactionDate"), endDate);
        };
    }

    //esse metodo vai filtrar as transações que tem a categoria correspondente ao parâmetro
    public static Specification<Transaction> byCategory(Category category){
        return (root, query, cb) ->{
          if (category == null){
              return cb.conjunction();
          }
          return cb.equal(root.get("category"), category);
        };
    }

    //esse metodo vai filtrar as transações que tem o tipo correspondente ao parâmetro
    public static Specification<Transaction> byType(TypeTransaction type) {
        return (root, query, cb) ->{
            if (type == null){
                return cb.conjunction();
            }
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Transaction> byPaymentMethod(PaymentMethod paymentMethod){
        return (root, query, cb) ->{
            if (paymentMethod == null){
                return cb.conjunction();
            }
            return cb.equal(root.get("paymentMethod"), paymentMethod);
        };
    }

}
