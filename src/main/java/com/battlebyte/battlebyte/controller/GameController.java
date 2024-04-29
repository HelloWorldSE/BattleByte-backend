package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.UserGameRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/game")
@RestController
public class GameController {
    @Autowired
    private GameService gameService;
    @Autowired
    private UserGameRecordService userGameRecordService;

    @PostMapping("/add")
    public void addGame(@RequestBody Game game) {
        gameService.addGame(game);
    }

    @DeleteMapping("/{gameId}")
    public void delGame(@PathVariable("gameId") Integer gameId) {
        gameService.delGame(gameId);
    }

    @GetMapping("")
    public Game getGame(Integer id) {
        return gameService.getGame(id);
    }

    @PostMapping("/update")
    public void updateGame(@RequestBody Game game) {
        gameService.updateGame(game);
    }

    @GetMapping("/player")
    public Page<UserInfoDTO> getPlayer(@RequestParam Integer id, @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return gameService.getPlayer(id, PageRequest.of(page - 1, pageSize));
    }

    @GetMapping("/update-record")
    public void save(@RequestBody UserGameRecord userGameRecord) {
        userGameRecordService.save(userGameRecord);
    }

    @DeleteMapping("/{id}")
    public void del(@PathVariable("id") Integer id) {
        userGameRecordService.del(id);
    }
}
