package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface GameDao extends JpaRepository<Game, Integer> {
    @Query(value = "select user.id, user_name as userName, avatar, team, question_id as questionId " +
            "from user, user_game_record where user.id = user_game_record.user_id and user_game_record.game_id = ?1",
            nativeQuery = true)
    public Page<UserGameDTO> getPlayer(Integer id, Pageable pageable);

    @Query(value = "select user.id, user_name as userName, avatar, team, question_id as questionId " +
            "from user, user_game_record where user.id = user_game_record.user_id and user_game_record.game_id = ?1",
            nativeQuery = true)
    public List<UserGameDTO> getPlayer(Integer id);
}
