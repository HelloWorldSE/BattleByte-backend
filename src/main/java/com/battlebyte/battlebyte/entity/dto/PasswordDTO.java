package com.battlebyte.battlebyte.entity.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class PasswordDTO {
    @Id
    private Integer id;
    private Integer verify;
    private String password;
}
