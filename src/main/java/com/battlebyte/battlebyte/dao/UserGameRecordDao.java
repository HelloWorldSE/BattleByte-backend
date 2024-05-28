package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.UserGameRecord;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameRecordDao extends JpaRepository<UserGameRecord, Integer> {
    public UserGameRecord findByGameIdAndUserId(Integer gameId, Integer userId);
    public void deleteByGameIdAndUserId(Integer gameId, Integer userId);
}
