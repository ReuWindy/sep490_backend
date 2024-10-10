package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "contract_time")// Số hợp đồng
    private Date contractTime;

    @Column(name = "contract_amount")// thời gian ký kết
    private double amount;          // Giá trị hợp đồng

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    private String pdfFilePath;     // Đường dẫn tệp PDF
    private String imageFilePath;   // Đường dẫn tệp ảnh
    private boolean confirmed;
    private Date confirmationDate;
}