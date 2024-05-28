package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Friend")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "smallId",nullable = false)
    private Integer smallId;
    @Column(name = "largeId",nullable = false)
    private Integer largeId;
}
