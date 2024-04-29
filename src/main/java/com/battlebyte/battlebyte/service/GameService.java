package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.dao.UserGameRecordDao;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameDao gameDao;

    @Autowired
    public UserGameRecordDao userGameRecordDao;

    // 添加游戏
    public void addGame(Game game) {
        game.setDate(new Date());
        gameDao.save(game);
    }

    public void delGame(Integer gameId) {
        gameDao.deleteById(gameId);
    }

    public Game getGame(Integer id) {
        Optional<Game> op = gameDao.findById(id);
        return op.orElse(null);
    }

    public void updateGame(Game game) {
        gameDao.save(game);
    }

    public Page<UserGameDTO> getPlayer(Integer id, Pageable pageable) {
        return gameDao.getPlayer(id, pageable);
    }

    public void save(UserGameRecord userGameRecord) {
        userGameRecordDao.save(userGameRecord);
    }

    public void del(Integer id) {
        userGameRecordDao.deleteById(id);
    }
}
