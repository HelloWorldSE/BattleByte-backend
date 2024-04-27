package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    public Page<UserInfoDTO> getPlayer(Integer id, Pageable pageable) {
        return gameDao.getPlayer(id, pageable);
    }
}
