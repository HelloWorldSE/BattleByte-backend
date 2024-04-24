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
    @Column(name = "sender_id",nullable = false)
    private Integer senderId;
    @Column(name = "receiver_id",nullable = false)
    private Integer receiverId;
}
