package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDao extends JpaRepository<Game, Integer> {
}
