package com.example.flappybird.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ScoreResponse {
    private int userId;
    private String username;
    private int score;
    private Date createdAt;
}