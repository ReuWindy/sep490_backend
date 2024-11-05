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

    @Column(name = "contract_number")// Số hợp đồng
    private String contractNumber;

    @Column(name = "contract_time")// ngày tạo
    private Date contractTime;

    @Column(name = "contract_duration")
    private int contractDuration;

    @Column(name = "contract_amount")// Giá trị hợp đồng
    private double amount;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    private String pdfFilePath;     // Đường dẫn tệp PDF
    private String imageFilePath;   // Đường dẫn tệp ảnh
    private boolean confirmed;
    private Date confirmationDate;// thời gian ký kết
}