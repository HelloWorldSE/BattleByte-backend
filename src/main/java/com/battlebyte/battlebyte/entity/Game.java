package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Game")
public class Game {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "gameType",nullable = false)
    private Integer gameType;
}
