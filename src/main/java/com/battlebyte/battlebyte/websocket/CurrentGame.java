package com.battlebyte.battlebyte.websocket;

import cn.hutool.core.collection.LineIter;
import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.UserService;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Data
public class CurrentGame {
    int gameId;
    ArrayList<Integer> questionId;
    LocalDateTime currentTime;
    private Integer gameType; //1是单人，2是大逃杀

    //队号，用户id
    Map<String, Integer> playerMap;

    //用户id，HP
    Map<Integer, Integer> HPMAP;

    //用户id，ac的题目数
    Map<Integer, Integer> acMAP;

    int currentQuestion = 0;

    private UserService userService;

    public CurrentGame() {
        userService = BeanContext.getApplicationContext().getBean(UserService.class);
    }

    Map<String, Integer> getHP() {
        Map<String, Integer> hpMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : HPMAP.entrySet()) {
            hpMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return hpMap;
    }

    Map<String, Integer> getAc() {
        Map<String, Integer> aMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : acMAP.entrySet()) {
            aMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return aMap;
    }

    Map<String, String> getName() {
        Map<String, String> nameMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : playerMap.entrySet()) {
            UserProfileDTO user = userService.findByUserId(entry.getValue());
            nameMap.put(entry.getKey(), user.getUserName());
        }
        return nameMap;
    }

    public boolean isInGame(int playerId) {
        if (HPMAP.get(playerId) != 0)
            return true;
        else
            return false;
    }


}