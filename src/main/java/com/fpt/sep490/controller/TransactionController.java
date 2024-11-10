package com.fpt.sep490.controller;

import com.fpt.sep490.dto.TransactionDto;
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
import java.util.Set;

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

    @GetMapping("/{receiptId}")
    public ResponseEntity<?> getTransactionByReceiptId(@PathVariable long receiptId) {
       Set<TransactionDto> transaction = transactionService.getTransactionByReceiptId(receiptId);
        if(transaction != null){
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Get Transaction Failed",HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/createTransaction")
    public ResponseEntity<?> createTransaction( @RequestBody TransactionDto transactionDto) {
       Transaction createdTransaction = transactionService.createTransactionByAdmin(transactionDto);
        if(createdTransaction != null){
            return ResponseEntity.status(HttpStatus.OK).body(createdTransaction);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Created Transaction Failed",HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateTransaction")
    public ResponseEntity<?> updateTransaction(@RequestBody TransactionDto transactionDto) {
      Transaction updatedTransaction = transactionService.updateTransaction(transactionDto);
        if(updatedTransaction != null){
            return ResponseEntity.status(HttpStatus.OK).body(updatedTransaction);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Update Transaction Failed",HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
