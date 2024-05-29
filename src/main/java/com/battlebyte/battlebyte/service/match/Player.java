package com.battlebyte.battlebyte.service.match;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private Integer userId;
    private Integer rating;
    private Integer waitingTime;
}