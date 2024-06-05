package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.GameQuestionRecord;
import com.battlebyte.battlebyte.entity.Room;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/game")
@RestController
public class GameController {
    @Autowired
    private GameService gameService;

    /* 通过room_id查询房间人数 */
    @GetMapping("/player/id")
    public Page<UserGameDTO> getPlayer(@RequestParam Integer id, @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return gameService.getPlayer(id, PageRequest.of(page - 1, pageSize));
    }

    // TODO: 注意权限问题！
    @GetMapping("/player")
    public List<UserGameDTO> getPlayer(@RequestParam Integer id) {
        return gameService.getPlayer(id);
    }

    @GetMapping("/count")
    public Integer count(Integer id) {
        return gameService.countByRoomId(id);
    }

    @PostMapping("/gameadd")
    public void gameAdd(@RequestBody GameQuestionRecord gameQuestionRecord) {
        if (!gameService.inGame(JwtUtil.getUserId(), gameQuestionRecord.getGameId())) {
            throw new ServiceException("无权限");
        }
        gameQuestionRecord.setId(null);
        gameService.save(gameQuestionRecord);
    }

    @DeleteMapping("/gamedelete/{id}")
    public void gameDelete(@PathVariable("id") Integer id) {
        GameQuestionRecord gameQuestionRecord = gameService.findGQRById(id);
        if (!gameService.inGame(JwtUtil.getUserId(), gameQuestionRecord.getGameId())) {
            throw new ServiceException("无权限");
        }
        gameService.delGameQuestionRecord(id);
    }
}
