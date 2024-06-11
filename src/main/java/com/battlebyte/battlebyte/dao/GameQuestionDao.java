package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.GameQuestionRecord;
import com.battlebyte.battlebyte.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameQuestionDao extends JpaRepository<GameQuestionRecord, Integer> {
    List<GameQuestionRecord> findByGameId(Integer gameId);
    void deleteByGameIdAndQuestionId(Integer gid, Integer qid);
}
