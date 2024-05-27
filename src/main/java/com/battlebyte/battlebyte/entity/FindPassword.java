package com.battlebyte.battlebyte.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPassword {
    @Id
    private Integer id;
    private Integer verify;
    private Date date;
}
