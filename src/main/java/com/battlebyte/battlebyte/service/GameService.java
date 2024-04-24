package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameDao gameDao;

    // 添加游戏
    public void addGame(Game game) {
        gameDao.save(game);
    }

    public void delGame(Integer gameId) {
        gameDao.deleteById(gameId);
    }

    public void updateGame(Game game) {
        gameDao.save(game);
    }
}
