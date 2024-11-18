package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import com.fpt.sep490.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
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

    private TransactionDto convertToDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTransactionDate(transaction.getTransactionDate());
        transactionDto.setPaymentMethod(transaction.getPaymentMethod());
        return transactionDto;
    }
}
