package com.example.flappybird.controller;

import com.example.flappybird.model.Score;
import com.example.flappybird.repository.ScoreRepository;
import com.example.flappybird.response.HistoryResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ScoreController {
    private ScoreRepository scoreRepository;

    @PostMapping("/score")
    public ResponseEntity<String> saveScore(@RequestBody Score score) {
        score.setCreatedAt(new Date());
        try {
            scoreRepository.save(score);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving score", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Saved score", HttpStatus.OK);
    }

    @GetMapping("/score/history")
    public ResponseEntity<List<HistoryResponse>> getScoreHistory(@RequestParam Integer userId) {
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Score> res;
        try {
            res = scoreRepository.findTop6ScoresByUserId(userId, Pageable.ofSize(6));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        String username = scoreRepository.findUsernameByUserId(userId);
        List<HistoryResponse> responses = res.stream().map(history -> new HistoryResponse(
                history.getUserId(),
                history.getScore(),
                username,
                history.getCreatedAt()
        )).toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/score/history-by-date")
    public ResponseEntity<List<HistoryResponse>> getScoreHistoryByDate(
            @RequestParam Integer userId,
            @RequestParam String date // Format: "yyyy-MM-dd"
    ) {
        if (userId == null || date == null || date.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate localDate = LocalDate.parse(date);
            Date startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<Score> scores = scoreRepository.findScoresByUserIdAndCreatedAtBetween(userId, startDate, endDate);
            String username = scoreRepository.findUsernameByUserId(userId);

            List<HistoryResponse> responses = scores.stream().map(score -> new HistoryResponse(
                    score.getUserId(),
                    score.getScore(),
                    username,
                    score.getCreatedAt()
            )).toList();

            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}