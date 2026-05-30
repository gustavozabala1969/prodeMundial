package com.prode.service;

import com.prode.dto.*;
import com.prode.entity.*;
import com.prode.entity.Match.Phase;
import com.prode.entity.Match.MatchStatus;
import com.prode.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final FechaTopeRepository fechaTopeRepository;

    // Constantes de puntos
    public static final int POINTS_EXACT  = 3;
    public static final int POINTS_RESULT = 1;
    public static final int POINTS_WRONG  = 0;

    /** Partidos de fase de grupos con pronóstico del usuario */
    public List<MatchResponse> getGroupMatches(Long userId) {
        List<Match> matches = matchRepository.findByPhaseInOrderByMatchDateAscMatchTimeAsc(
        List.of(Match.Phase.F1, Match.Phase.F2, Match.Phase.F3));
        Map<Long, Prediction> userPreds = getUserPredMap(userId);
        return matches.stream().map(m -> toResponse(m, userPreds.get(m.getId()))).toList();
    }

    public List<MatchResponse> getGroupMatchesPorFecha(Long userId, Phase fase) {
        List<Match> matches = matchRepository.findByPhaseOrderByMatchDateAscMatchTimeAsc(fase);
        Map<Long, Prediction> userPreds = getUserPredMap(userId);
        return matches.stream().map(m -> toResponse(m, userPreds.get(m.getId()))).toList();
    }

    /** Todos los partidos (incluye eliminatorias) con pronóstico del usuario */
    public List<MatchResponse> getAllMatches(Long userId) {
        List<Match> matches = matchRepository.findAllByOrderByMatchDateAscMatchTimeAsc();
        Map<Long, Prediction> userPreds = getUserPredMap(userId);
        return matches.stream().map(m -> toResponse(m, userPreds.get(m.getId()))).toList();
    }

    /** Guardar o actualizar pronóstico (solo si el partido no terminó) */
    @Transactional
    public void savePrediction(Long userId, SavePredictionRequest req) {


        Match match = matchRepository.findById(req.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));

        if (match.getStatus() == MatchStatus.FINISHED) {
            throw new IllegalStateException("El partido ya terminó. No se puede modificar el pronóstico.");
        }

        FechaTopePrediction fechaTope = fechaTopeRepository
            .findById(match.getPhase().name())
            .orElseThrow(() -> new IllegalStateException(
                    "No existe fecha tope para fase " + match.getPhase().name()
            ));

        LocalDateTime fechaTopePrediccion = fechaTope.getFechaTopePrediction();
        if (LocalDateTime.now().isAfter(fechaTopePrediccion)) {
            throw new IllegalStateException(
                "La fecha límite para pronosticar esta fase ya venció."
            );
        }

        if (!(match.getPhase() == Phase.F1 || match.getPhase()==Phase.F2 || match.getPhase()==Phase.F3)) {
            throw new IllegalStateException("Solo se pueden cargar pronósticos de la fase de grupos (fecha 1, 2 y 3).");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Prediction pred = predictionRepository.findByUserIdAndMatchId(userId, req.getMatchId())
                .orElse(Prediction.builder().user(user).match(match).build());

        pred.setHomeScore(req.getHomeScore());
        pred.setAwayScore(req.getAwayScore());
        predictionRepository.save(pred);
    }

    /** ADMIN: cargar resultado real y calcular puntos */
    @Transactional
    public void setResult(SetResultRequest req) {
        Match match = matchRepository.findById(req.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));

        match.setHomeScore(req.getHomeScore());
        match.setAwayScore(req.getAwayScore());
        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);

        // Actualizar puntos de todos los pronósticos de este partido
        List<Prediction> preds = predictionRepository.findByMatchId(req.getMatchId());
        for (Prediction p : preds) {
            int pts = calcPoints(p.getHomeScore(), p.getAwayScore(), req.getHomeScore(), req.getAwayScore());
            p.setPoints(pts);
        }
        predictionRepository.saveAll(preds);
    }

    /** Ranking general */
    public List<RankingEntry> getRanking() {
        List<Object[]> raw = predictionRepository.getRankingRaw();
        List<RankingEntry> ranking = new ArrayList<>();
        for (int i = 0; i < raw.size(); i++) {
            Object[] r = raw.get(i);
            ranking.add(new RankingEntry(
                    i + 1,
                    ((Number) r[0]).longValue(),
                    (String) r[1],
                    (String) r[2],
                    ((Number) r[3]).longValue(),
                    ((Number) r[4]).longValue(),
                    ((Number) r[5]).longValue(),
                    ((Number) r[6]).longValue()
            ));
        }
        return ranking;
    }

    /** Comparar pronósticos entre dos usuarios (partidos con resultado final) */
    public List<CompareEntry> compare(Long myId, Long otherId) {
        Map<Long, Prediction> myPreds    = getUserPredMap(myId);
        Map<Long, Prediction> otherPreds = getUserPredMap(otherId);

        List<Match> finished = matchRepository.findAllByOrderByMatchDateAscMatchTimeAsc()
                .stream()
                .filter(m -> m.getStatus() == MatchStatus.FINISHED)
                .toList();

        List<CompareEntry> result = new ArrayList<>();
        for (Match m : finished) {
            CompareEntry e = new CompareEntry();
            e.setMatchId(m.getId());
            e.setHomeTeam(m.getHomeTeam());
            e.setHomeFlag(m.getHomeFlag());
            e.setAwayTeam(m.getAwayTeam());
            e.setAwayFlag(m.getAwayFlag());
            e.setRealHome(m.getHomeScore());
            e.setRealAway(m.getAwayScore());
            e.setMatchDate(m.getMatchDate() != null ? m.getMatchDate().toString() : "");
            e.setGroupName(m.getGroupName());
            e.setPhase(m.getPhase().name());

            Prediction myP = myPreds.get(m.getId());
            if (myP != null) {
                e.setMyHome(myP.getHomeScore());
                e.setMyAway(myP.getAwayScore());
                e.setMyPoints(myP.getPoints());
            }

            Prediction otherP = otherPreds.get(m.getId());
            if (otherP != null) {
                e.setOtherHome(otherP.getHomeScore());
                e.setOtherAway(otherP.getAwayScore());
                e.setOtherPoints(otherP.getPoints());
            }
            result.add(e);
        }
        return result;
    }

    // ---- Helpers ----

    private int calcPoints(int predH, int predA, int realH, int realA) {
        if (predH == realH && predA == realA) return POINTS_EXACT;
        int predResult = Integer.compare(predH, predA);
        int realResult = Integer.compare(realH, realA);
        if (predResult == realResult) return POINTS_RESULT;
        return POINTS_WRONG;
    }

    private Map<Long, Prediction> getUserPredMap(Long userId) {
        Map<Long, Prediction> map = new HashMap<>();
        predictionRepository.findByUserIdWithMatches(userId)
                .forEach(p -> map.put(p.getMatch().getId(), p));
        return map;
    }

    private MatchResponse toResponse(Match m, Prediction pred) {
        MatchResponse r = new MatchResponse();
        r.setId(m.getId());
        r.setGroupName(m.getGroupName());
        r.setPhase(m.getPhase().name());
        r.setHomeTeam(m.getHomeTeam());
        r.setHomeFlag(m.getHomeFlag());
        r.setAwayTeam(m.getAwayTeam());
        r.setAwayFlag(m.getAwayFlag());
        r.setMatchDate(m.getMatchDate());
        r.setMatchTime(m.getMatchTime());
        r.setHomeScore(m.getHomeScore());
        r.setAwayScore(m.getAwayScore());
        r.setStatus(m.getStatus().name());
        if (pred != null) {
            r.setMyPredHome(pred.getHomeScore());
            r.setMyPredAway(pred.getAwayScore());
            r.setMyPoints(pred.getPoints());
        }
        return r;
    }
}
