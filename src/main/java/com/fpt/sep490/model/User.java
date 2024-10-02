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
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    @Column(unique = true)
    private long phoneNumber;
    @Column(unique = true)
    private String email;
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String address;
    private Date createAt;
    private Date updateAt;
    private boolean active;

    @ManyToOne
    @JoinColumn(name="user_type_id", referencedColumnName = "id")
    private UserType userType;
}
