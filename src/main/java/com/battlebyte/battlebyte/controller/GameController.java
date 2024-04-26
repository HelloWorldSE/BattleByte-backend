package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/player")
    public Page<UserInfoDTO> getPlayer(@RequestParam Integer id, @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return gameService.getPlayer(id, PageRequest.of(page - 1, pageSize));
    }

}
