package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer sender;
    private Integer receiver;
    private String message;
    private Date date;
    private Integer tag;
    private boolean read;
}
