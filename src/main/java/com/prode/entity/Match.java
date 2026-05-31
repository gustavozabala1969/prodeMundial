package com.prode.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 10)
    private String groupName;

    /** Fase: GROUP (F1, F2, F3), ROUND_OF_32, OCTAVOS, QUARTER, SEMI, TERCERO, FINAL */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Phase phase = Phase.GROUP;

    @Column(name = "home_team", nullable = false, length = 60)
    private String homeTeam;

    @Column(name = "home_flag", length = 10)
    private String homeFlag;

    @Column(name = "away_team", nullable = false, length = 60)
    private String awayTeam;

    @Column(name = "away_flag", length = 10)
    private String awayFlag;

    @Column(name = "match_date")
    private LocalDate matchDate;

    @Column(name = "match_time", length = 10)
    @Builder.Default
    private String matchTime = "16:00";

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Prediction> predictions;

    public enum Phase {
        GROUP, F1, F2, F3, ROUND_OF_32, ROUND_OF_16, QUARTER, SEMI, TERCERO, FINAL
    }

    public enum MatchStatus {
        PENDING, FINISHED
    }
}
