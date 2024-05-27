package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "Game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "gameType",nullable = false)
    private Integer gameType; //1是单人模式，2是大逃杀模式
    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private Date date;


}
