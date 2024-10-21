package com.fpt.sep490.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fpt.sep490.model.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDto {
    private String contractNumber;
    private double amount;
    private String customerName;
    private String pdfFilePath;
    private String imageFilePath;
    private Date confirmationDate;
}
