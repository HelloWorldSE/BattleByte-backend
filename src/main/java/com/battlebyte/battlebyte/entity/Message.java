package com.battlebyte.battlebyte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.Date;

public class Message {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer sender;
    private Integer receiver;
    private String message;
    private Date date;
}
