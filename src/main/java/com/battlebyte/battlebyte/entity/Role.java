package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    @Column(name = "rid")
    private Integer rid;

    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;
}