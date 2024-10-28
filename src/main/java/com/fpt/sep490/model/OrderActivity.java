package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "order_activities")
public class OrderActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "activity_type")
    private String activityType;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "user_perform")
    private String userPerform;
}
