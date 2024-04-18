package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rid")
    private Integer rid;

    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

    @ManyToMany // (mappedBy = "roles")
    private List<User> users;
}