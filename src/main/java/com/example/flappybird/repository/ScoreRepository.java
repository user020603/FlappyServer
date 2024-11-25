package com.example.flappybird.repository;

import com.example.flappybird.dto.MaxScoreDTO;
import com.example.flappybird.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Integer> {
    @Query("SELECT MAX(s.score) as maxScore, " +
            "MAX(s.createdAt) as createdAt, " +
            "u.id as userId " +
            "FROM Score s " +
            "JOIN User u ON s.userId = u.id " +
            "GROUP BY s.userId " +
            "ORDER BY maxScore DESC, createdAt DESC")
    Page<MaxScoreDTO> findTopScores(Pageable pageable);

    @Query("SELECT s FROM Score s WHERE s.userId = :userId ORDER BY s.createdAt DESC")
    List<Score> findTop6ScoresByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT u.username FROM User u WHERE u.id = :userId")
    String findUsernameByUserId(Integer userId);

    List<Score> findScoresByUserIdAndCreatedAtBetween(Integer userId, Date startDate, Date endDate);
}