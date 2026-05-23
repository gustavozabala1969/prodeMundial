package com.prode.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class RankingEntry {
    private int position;
    private Long userId;
    private String name;
    private String email;
    private long totalPoints;
    private long predCount;
    private long exactCount;
    private long resultCount;
}
