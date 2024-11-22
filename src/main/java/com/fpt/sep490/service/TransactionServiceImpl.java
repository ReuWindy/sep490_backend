package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.RevenueStatisticsView;
import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import com.fpt.sep490.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final ReceiptVoucherRepository receiptVoucherRepository;

    public TransactionServiceImpl (TransactionRepository transactionRepository,ReceiptVoucherRepository receiptVoucherRepository){
        this.transactionRepository = transactionRepository;
        this.receiptVoucherRepository = receiptVoucherRepository;
    }
    @Override
    public Transaction updateTransaction(TransactionDto transactionDto) {
        Transaction updatedTransaction = transactionRepository.findById(transactionDto.getId()).orElseThrow(()->new RuntimeException("Transaction Not Found !"));
        ReceiptVoucher receiptVoucher = receiptVoucherRepository.findById(transactionDto.getReceiptVoucherId()).orElseThrow(()->new RuntimeException("ReceiptVoucher Not Found !"));
        Date currentDate = new Date();
        long threeDaysAgoMillis = currentDate.getTime() - (3L * 24 * 60 * 60 * 1000);
        if((transactionDto.getTransactionDate().getTime() > threeDaysAgoMillis)) {
            if(updatedTransaction != null) {
                if(transactionDto.getAmount() <= 0){
                    throw new RuntimeException("Số tiền giao dịch phải là số dương");
                }
                updatedTransaction.setAmount(transactionDto.getAmount());
                updatedTransaction.setTransactionDate(transactionDto.getTransactionDate());
                if(transactionDto.getPaymentMethod() == null){
                    throw new RuntimeException("Phương thức thanh toán không được để trống");
                }
                updatedTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
                transactionRepository.save(updatedTransaction);

                double newPaidAmount = receiptVoucher.getPaidAmount() + transactionDto.getAmount();
                double newRemainAmount = receiptVoucher.getTotalAmount() - newPaidAmount;
                receiptVoucher.setPaidAmount(newPaidAmount);
                receiptVoucher.setRemainAmount(newRemainAmount);
                try {
                    receiptVoucherRepository.save(receiptVoucher);
                    return updatedTransaction;
                }catch (Exception e){
                    throw new RuntimeException("Xảy ra lỗi khi lưu giao dịch !");
                }
            }
        }else {
            throw new RuntimeException("Giao dịch đã quá hạn và không thể cập nhật.");
        }
        return null;
    }

    @Override
    public Set<TransactionDto> getTransactionByReceiptId(long receiptId) {
        Set<Transaction> transactions = transactionRepository.findByReceiptVoucher_Id(receiptId);
        return transactions.stream().map(this::convertToDto).collect(Collectors.toSet());
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction createTransactionByAdmin(TransactionDto transactionDto) {
        Transaction createdTransaction = new Transaction();
        ReceiptVoucher receiptVoucher = receiptVoucherRepository.findById(transactionDto.getReceiptVoucherId()).orElseThrow(()-> new RuntimeException("ReceiptVoucher Not Found !"));
        if (receiptVoucher != null) {
            receiptVoucher.setPaidAmount(receiptVoucher.getPaidAmount() + transactionDto.getAmount());
            receiptVoucher.setRemainAmount(receiptVoucher.getTotalAmount() - receiptVoucher.getPaidAmount());
            createdTransaction.setReceiptVoucher(receiptVoucher);
            if(transactionDto.getAmount() <= 0){
                throw new RuntimeException("Số tiền giao dịch phải là số dương");
            }
            createdTransaction.setAmount(transactionDto.getAmount());
            createdTransaction.setTransactionDate(new Date());
            if(transactionDto.getPaymentMethod() == null){
                throw new RuntimeException("Phương thức thanh toán không được để trống");
            }
            createdTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
            createdTransaction.setStatus(StatusEnum.COMPLETED);
            transactionRepository.save(createdTransaction);

            double newPaidAmount = receiptVoucher.getPaidAmount() + transactionDto.getAmount();
            double newRemainAmount = receiptVoucher.getTotalAmount() - newPaidAmount;
            receiptVoucher.setPaidAmount(newPaidAmount);
            receiptVoucher.setRemainAmount(newRemainAmount);
            try {
                receiptVoucherRepository.save(receiptVoucher);
                return createdTransaction;
            }catch (Exception e){
                throw new RuntimeException("Xảy ra lỗi khi tạo giao dịch mới");
            }
        }

        return null;
    }

    @Override
    public RevenueStatisticsView getRevenueStatistics(String timeFilter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        DateTimeFormatter formatter = switch (timeFilter.toLowerCase()) {
            case "week" -> {
                startDate = now.minusDays(6);
                yield DateTimeFormatter.ofPattern("yyyy-MM-dd");
            }
            case "month" -> {
                startDate = now.minusDays(29);
                yield DateTimeFormatter.ofPattern("yyyy-MM-dd");
            }
            case "year" -> {
                startDate = now.minusMonths(11).withDayOfMonth(1);
                yield DateTimeFormatter.ofPattern("yyyy-MM");
            }
            default -> throw new IllegalArgumentException("Bộ lọc không hợp lệ: " + timeFilter);
        };

        List<Transaction> transactions = transactionRepository.findAllByTransactionDateBetweenAndStatus(
                java.sql.Date.valueOf(startDate.toLocalDate()),
                java.sql.Date.valueOf(now.toLocalDate().plusDays(1)),
                StatusEnum.COMPLETED);

        Map<String, Double> revenueMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formatter),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        List<RevenueStatisticsView.RevenueDetail> details = revenueMap.entrySet().stream()
                .map(entry -> RevenueStatisticsView.RevenueDetail.builder()
                        .timePeriod(entry.getKey())
                        .revenue(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(RevenueStatisticsView.RevenueDetail::getTimePeriod))
                .collect(Collectors.toList());

        double totalRevenue = details.stream()
                .mapToDouble(RevenueStatisticsView.RevenueDetail::getRevenue)
                .sum();

        return RevenueStatisticsView.builder()
                .totalRevenue(totalRevenue)
                .details(details)
                .build();
    }

    private TransactionDto convertToDto(Transaction transaction){
        return TransactionDto.toDto(transaction);
    }
}
