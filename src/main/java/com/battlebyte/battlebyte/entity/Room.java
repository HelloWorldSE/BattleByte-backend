package com.battlebyte.battlebyte.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer gameId;
    private Integer uid;
    /**
     * <b>0</b>: 未开始
     * <p>
     * <b>1</b>: 正在进行
     * <p>
     * <b>2</b>: 已经结束
     */
    @Column(name = "status")
    private Integer status;
}
