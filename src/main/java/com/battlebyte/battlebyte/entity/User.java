package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "userName", length = 20,nullable = false,unique = true)
    private String userName;
    @Column(name = "password", length = 20,nullable = false)
    private String password;
    @Column(name = "userEmail", length = 30,nullable = false)
    private String userEmail;
    @Column(name = "avatar", length = 100)
    private String avatar; //图片路径
    @Column(name = "rating")
    private Integer rating;
}
