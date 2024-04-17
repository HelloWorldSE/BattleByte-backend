package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue
    @Column(name = "pid")
    private Integer pid;

    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

}