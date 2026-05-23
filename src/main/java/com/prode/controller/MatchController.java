package com.prode.controller;

import com.prode.dto.*;
import com.prode.repository.UserRepository;
import com.prode.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final UserRepository userRepository;

    /** Partidos fase de grupos con mi pronóstico */
    @GetMapping("/matches/group")
    public ResponseEntity<List<MatchResponse>> getGroupMatches(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(matchService.getGroupMatches(userId));
    }

    /** Todos los partidos (para ver resultados finales) */
    @GetMapping("/matches/all")
    public ResponseEntity<List<MatchResponse>> getAllMatches(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(matchService.getAllMatches(userId));
    }

    /** Guardar pronóstico */
    @PostMapping("/predictions")
    public ResponseEntity<?> savePrediction(@AuthenticationPrincipal UserDetails userDetails,
                                            @Valid @RequestBody SavePredictionRequest req) {
        try {
            Long userId = getUserId(userDetails);
            matchService.savePrediction(userId, req);
            return ResponseEntity.ok(Map.of("message", "Pronóstico guardado correctamente."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Ranking general en tiempo real */
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingEntry>> getRanking() {
        return ResponseEntity.ok(matchService.getRanking());
    }

    /** Lista de usuarios para comparar */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserDetails userDetails) {
        Long myId = getUserId(userDetails);
        var users = userRepository.findAll().stream()
                .filter(u -> !u.isAdmin() && !u.getId().equals(myId))
                .map(u -> Map.of("id", u.getId(), "name", u.getName()))
                .toList();
        return ResponseEntity.ok(users);
    }

    /** Comparar mis pronósticos con otro usuario */
    @GetMapping("/compare/{otherId}")
    public ResponseEntity<List<CompareEntry>> compare(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable Long otherId) {
        Long myId = getUserId(userDetails);
        return ResponseEntity.ok(matchService.compare(myId, otherId));
    }

    // ---- ADMIN ----

    @PostMapping("/admin/result")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setResult(@Valid @RequestBody SetResultRequest req) {
        try {
            matchService.setResult(req);
            return ResponseEntity.ok(Map.of("message", "Resultado cargado y puntos actualizados."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/matches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponse>> getAdminMatches() {
        return ResponseEntity.ok(matchService.getAllMatches(null));
    }

    // ---- Helper ----
    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow().getId();
    }
}
