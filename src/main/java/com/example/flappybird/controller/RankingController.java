package com.example.flappybird.controller;

import com.example.flappybird.dto.MaxScoreDTO;
import com.example.flappybird.response.ScoreResponse;
import com.example.flappybird.repository.ScoreRepository;
import com.example.flappybird.model.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RankingController {

    @Autowired
    private ScoreRepository scoreRepository;

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Kiểm tra tham số hợp lệ
        if (page < 0 || size <= 0 || size > 100) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid page or size parameter"));
        }

        // Lấy dữ liệu phân trang
        Page<MaxScoreDTO> topScoresPage = scoreRepository.findTopScores(PageRequest.of(page, size));
        List<ScoreResponse> scoreResponses = topScoresPage.getContent().stream().map(score -> new ScoreResponse(
                score.getUserId(),
                scoreRepository.findUsernameByUserId(score.getUserId()),
                score.getMaxScore(),
                score.getCreatedAt()
        )).collect(Collectors.toList());

        // Đóng gói kết quả trả về
        Map<String, Object> response = new HashMap<>();
        response.put("scores", scoreResponses);
        response.put("currentPage", topScoresPage.getNumber()); // `page` hiện tại
        response.put("totalItems", topScoresPage.getTotalElements()); // Tổng số mục
        response.put("totalPages", topScoresPage.getTotalPages()); // Tổng số trang

        return ResponseEntity.ok(response);
    }

}