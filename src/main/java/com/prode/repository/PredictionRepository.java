package com.prode.repository;

import com.prode.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Optional<Prediction> findByUserIdAndMatchId(Long userId, Long matchId);

    List<Prediction> findByUserId(Long userId);

    List<Prediction> findByMatchId(Long matchId);

    @Query("""
        SELECT p FROM Prediction p
        JOIN FETCH p.match m
        JOIN FETCH p.user u
        WHERE p.match.id = :matchId
        ORDER BY u.name
    """)
    List<Prediction> findByMatchIdWithUsers(@Param("matchId") Long matchId);

    @Query("""
        SELECT p FROM Prediction p
        JOIN FETCH p.match m
        WHERE p.user.id = :userId
    """)
    List<Prediction> findByUserIdWithMatches(@Param("userId") Long userId);

    /** Ranking: suma de puntos por usuario */
    @Query("""
        SELECT u.id, u.name, u.email,
               COALESCE(SUM(p.points), 0) as totalPoints,
               COUNT(p.id) as predCount,
               SUM(CASE WHEN p.points = 3 THEN 1 ELSE 0 END) as exactCount,
               SUM(CASE WHEN p.points = 1 THEN 1 ELSE 0 END) as resultCount
        FROM User u
        LEFT JOIN u.predictions p
        WHERE u.admin = false
        GROUP BY u.id, u.name, u.email
        ORDER BY totalPoints DESC, exactCount DESC, predCount DESC
    """)
    List<Object[]> getRankingRaw();
}
