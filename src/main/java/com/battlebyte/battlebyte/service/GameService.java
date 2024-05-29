package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.dao.GameQuestionDao;
import com.battlebyte.battlebyte.dao.UserGameRecordDao;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.GameQuestionRecord;
import com.battlebyte.battlebyte.entity.Question;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service

public class GameService {
    @Autowired
    private GameDao gameDao;

    @Autowired
    public UserGameRecordDao userGameRecordDao;

    @Autowired
    public GameQuestionDao gameQuestionDao;

    // 添加游戏
    public void addGame(Game game) {
        game.setDate(new Date());
        gameDao.save(game);
    }

    public Game addBlankGame() {
        Game game = new Game();
        game.setGameType(0);
        game.setDate(new Date());
        return gameDao.save(game);
    }

    // 删除游戏
    public void delGame(Integer gameId) {
        gameDao.deleteById(gameId);
    }

    // 获取游戏
    public Game getGame(Integer id) {
        Optional<Game> op = gameDao.findById(id);
        return op.orElse(null);
    }

    // 更新游戏
    public void updateGame(Game game) {
        gameDao.save(game);
    }

    // 从游戏中获取玩家
    public Page<UserGameDTO> getPlayer(Integer id, Pageable pageable) {
        return gameDao.getPlayer(id, pageable);
    }

    public List<UserGameDTO> getPlayer(Integer id) {
        return gameDao.getPlayer(id);
    }

    // 保存userGameRecord记录
    public void save(UserGameRecord userGameRecord) {
        if (userGameRecordDao.findByGameIdAndUserId(userGameRecord.getGameId(), userGameRecord.getUserId()) != null) {
            throw new ServiceException("已存在此用户，添加失败");
        }
        userGameRecordDao.save(userGameRecord);
    }

    // 删除userGameRecord记录
    public void delUserGameRecord(Integer id) {
        userGameRecordDao.deleteById(id);
    }

    // 保存gameQuestionRecord记录
    public void save(GameQuestionRecord gameQuestionRecord) {
        gameQuestionDao.save(gameQuestionRecord);
    }

    // 删除gameQuestionRecord记录
    public void delGameQuestionRecord(Integer id) {
        gameQuestionDao.deleteById(id);
    }

/*  -----------------   复杂功能  ------------------- */
    public void deleteByGameIdAndUserId(Integer gameId, Integer userId) {
        userGameRecordDao.deleteByGameIdAndUserId(gameId, userId);
    }

    public UserGameRecord findByGameIdAndUserId(Integer gameId, Integer userId) {
        return userGameRecordDao.findByGameIdAndUserId(gameId, userId);
    }

    public void setTeam(Integer gameId, Integer userId, Integer team) {
        UserGameRecord userGameRecord = userGameRecordDao.findByGameIdAndUserId(gameId, userId);
        userGameRecord.setTeam(team);
        userGameRecordDao.save(userGameRecord);
    }

    public List<Question> findByGameId(Integer gameId) {
        return gameQuestionDao.findByGameId(gameId);
    }
}
