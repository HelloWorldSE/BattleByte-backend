package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface GameDao extends JpaRepository<Game, Integer> {
    @Query(value = "select id, user_name as userName, avatar from user, ", nativeQuery = true)
    public Page<UserInfoDTO> getPlayer(Integer id, Pageable pageable);
}
