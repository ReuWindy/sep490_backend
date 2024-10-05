package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String address;
    private Date createAt;
    private Date updateAt;
    private boolean active;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private UserType userType;

}
