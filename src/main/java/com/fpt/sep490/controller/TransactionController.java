package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.service.TransactionService;
import com.google.api.Http;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController (TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransaction() {
        List<Transaction> transactions = transactionService.getAllTransaction();
        if(transactions != null){
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Get All Failed",HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable int id) {
       Transaction transaction = transactionService.getTransactionById(id);
        if(transaction != null){
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Get Transaction Failed",HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/createCategory")
    public ResponseEntity<?> createTransaction(HttpServletRequest request, @RequestBody Transaction transaction) {
       return null;
    }

    @PostMapping("/updateCategory")
    public ResponseEntity<?> updateTransaction(HttpServletRequest request, @RequestBody Transaction transaction) {
      Transaction updatedTransaction = transactionService.updateTransaction(transaction);
        if(transaction != null){
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Update Transaction Failed",HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
