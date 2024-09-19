package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String contractNumber; // Số hợp đồng
    private Date contractTime;  // thời gian ký kết
    private double amount;          // Giá trị hợp đồng

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String pdfFilePath;     // Đường dẫn tệp PDF
    private String imageFilePath;   // Đường dẫn tệp ảnh
    private boolean confirmed;
    private Date confirmationDate;
}