package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "game_question_record")
public class GameQuestionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "gameId",nullable = false)
    private Integer gameId;
    @Column(name = "questionId",nullable = false)
    private Integer questionId;
}
