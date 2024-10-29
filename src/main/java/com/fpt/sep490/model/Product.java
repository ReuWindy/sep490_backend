package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ và số.")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Chỉ có thể bao gồm các ký tự chữ và số.")
    private String description;

    @DecimalMin(value = "0", message = "Giá sản phẩm phải là số dương.")
    private double price;

    private String image;

    @Column(unique = true)
    private String productCode;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "unit_of_measure_id")
    private UnitOfMeasure unitOfMeasure;

    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = "Ngày tạo không thể là tương lai.")
    private Date createAt;

    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = "Ngày cập nhật không thể là tương lai.")
    private Date updateAt;

    private Boolean isDeleted;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductWarehouse> productWarehouses;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BatchProduct> batchProducts;

    @DecimalMin(value = "0", message = "Giá sản phẩm phải là số dương.")
    private double importPrice;
}
