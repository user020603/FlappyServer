package com.example.flappybird.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class HistoryResponse {
    private int userId;
    private int score;
    private String username;
    private Date createdAt;
}
