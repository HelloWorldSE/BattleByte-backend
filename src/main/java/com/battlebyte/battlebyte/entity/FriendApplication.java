package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "FriendApplication")
public class FriendApplication {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "senderId",nullable = false)
    private Integer senderId;
    @Column(name = "receiverId",nullable = false)
    private Integer receiverId;
}
