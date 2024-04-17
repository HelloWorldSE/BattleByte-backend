package com.battlebyte.battlebyte.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "UserGameRecord")
public class UserGameRecord {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "gameId",nullable = false)
    private Integer gameId;
    @Column(name = "userId",nullable = false)
    private Integer userId;
    @Column(name = "questionId",nullable = false)
    private Integer questionId;
}
