package com.prode.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class MatchResponse {
    private Long id;
    private String groupName;
    private String phase;
    private String homeTeam;
    private String homeFlag;
    private String awayTeam;
    private String awayFlag;
    private LocalDate matchDate;
    private String matchTime;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private Integer myPredHome;
    private Integer myPredAway;
    private Integer myPoints;
}
