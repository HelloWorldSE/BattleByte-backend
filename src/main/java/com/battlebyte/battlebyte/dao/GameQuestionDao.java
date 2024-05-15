package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.GameQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameQuestionDao extends JpaRepository<GameQuestionRecord, Integer> {
}
