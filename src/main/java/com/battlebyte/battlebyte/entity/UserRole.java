package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @Column(name = "uid")
    private Integer uid;

    @Id
    @Column(name = "rid")
    private Integer rid;

}
