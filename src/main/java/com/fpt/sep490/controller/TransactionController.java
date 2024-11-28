package com.fpt.sep490.controller;

import com.fpt.sep490.dto.RevenueStatisticsView;
import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.TransactionService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public TransactionController(TransactionService transactionService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.transactionService = transactionService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransaction();
        if (!transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{receiptId}")
    public ResponseEntity<?> getTransactionByReceiptId(@PathVariable long receiptId) {
        Set<TransactionDto> transaction = transactionService.getTransactionByReceiptId(receiptId);
        if (transaction != null && !transaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Transaction not found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStatistics(@RequestParam String timeFilter) {
        try {
            RevenueStatisticsView statisticsView = transactionService.getRevenueStatistics(timeFilter);
            return ResponseEntity.ok(statisticsView);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(HttpServletRequest request, @RequestBody TransactionDto transactionDto) {
        try {
            Transaction createdTransaction = transactionService.createTransactionByAdmin(transactionDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_TRANSACTION", "Tạo giao dịch: " + createdTransaction.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/transactions", "Giao dịch " + createdTransaction.getId() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateTransaction(HttpServletRequest request, @RequestBody TransactionDto transactionDto) {
        try {
            Transaction updatedTransaction = transactionService.updateTransaction(transactionDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_TRANSACTION", "Cập nhật giao dịch: " + updatedTransaction.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/transactions", " Giao dịch " + updatedTransaction.getId() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedTransaction);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}