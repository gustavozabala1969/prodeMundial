package com.prode.dto;

import lombok.Data;

@Data
public class CompareEntry {
    private Long matchId;
    private String homeTeam;
    private String homeFlag;
    private String awayTeam;
    private String awayFlag;
    private Integer realHome;
    private Integer realAway;
    private Integer myHome;
    private Integer myAway;
    private Integer myPoints;
    private Integer otherHome;
    private Integer otherAway;
    private Integer otherPoints;
    private String matchDate;
    private String groupName;
    private String phase;
}
