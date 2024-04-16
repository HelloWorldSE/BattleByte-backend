package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "User")
public class User {
    public enum Type {
        ADMIN,
        USER,
        GUEST
    }
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "userName", length = 20,nullable = false,unique = true)
    private String userName;
    @Column(name = "userEmail", length = 30,nullable = false)
    private String userEmail;
    @Column(name = "password", length = 20,nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "userType",nullable = false)
    private Type userType; //用户类型
    @Column(name = "avatar", length = 20)
    private String avatar; //图片路径
}
