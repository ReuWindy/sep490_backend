package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String message;
    private Date date;
    private boolean isRead = false;
}