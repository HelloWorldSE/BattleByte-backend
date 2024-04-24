package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update")
    public void updateGame(@RequestBody Game game) {
        gameService.updateGame(game);
    }

}
