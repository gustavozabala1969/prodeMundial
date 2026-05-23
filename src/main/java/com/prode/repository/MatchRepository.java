package com.prode.repository;

import com.prode.entity.Match;
import com.prode.entity.Match.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByPhaseOrderByMatchDateAscMatchTimeAsc(Phase phase);
    List<Match> findAllByOrderByMatchDateAscMatchTimeAsc();
}
