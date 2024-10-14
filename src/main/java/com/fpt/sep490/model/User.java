package com.fpt.sep490.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "user_name")
    private String username;

    @Column(name = "user_password")
    private String password;

    @Column(name = "phone_number",unique = true)
    private String phone;

    @Column(name = "user_email",unique = true)
    @Email
    private String email;

    @Column(name = "user_address", columnDefinition = "NVARCHAR(1000)")
    private String address;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "is_active")
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type",length = 50)
    private UserType userType;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "gender")
    private boolean gender;
}
