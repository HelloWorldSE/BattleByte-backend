package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.GameQuestionRecord;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: 该类可能有严重的权限问题！
 */

@RequestMapping("/api/game")
@RestController
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/add")
    public void addGame(@RequestBody Game game) {
        gameService.addGame(game);
    }

    @DeleteMapping("/{gameId}")
    public void delGame(@PathVariable("gameId") Integer gameId) {
        gameService.delGame(gameId);
    }

    @GetMapping("")
    public Game getGame(@RequestParam(defaultValue = "0") Integer id) {
        return gameService.getGame(id);
    }

    @PostMapping("/update")
    public void updateGame(@RequestBody Game game) {
        gameService.updateGame(game);
    }

    @GetMapping("/player")
    public Page<UserGameDTO> getPlayer(@RequestParam Integer id, @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return gameService.getPlayer(id, PageRequest.of(page - 1, pageSize));
    }

    @PostMapping("/update-record")
    public void save(@RequestBody UserGameRecord userGameRecord) {
        gameService.save(userGameRecord);
    }

    @PostMapping("/update-question")
    public void save2(@RequestBody GameQuestionRecord gameQuestionRecord) {
        gameService.save(gameQuestionRecord);
    }

    @DeleteMapping("/game/{id}")
    public void del(@PathVariable("id") Integer id) {
        gameService.delUserGameRecord(id);
    }

    @DeleteMapping("/question/{id}")
    public void del2(@PathVariable("id") Integer id) {
        gameService.delGameQuestionRecord(id);
    }
}
