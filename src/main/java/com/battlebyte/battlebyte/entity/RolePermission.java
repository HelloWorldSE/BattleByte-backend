package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "role_permission")
public class RolePermission {
    @Id
    @Column(name = "rid")
    private Integer rid;

    @Id
    @Column(name = "pid")
    private Integer pid;

    // Getters and setters

}

