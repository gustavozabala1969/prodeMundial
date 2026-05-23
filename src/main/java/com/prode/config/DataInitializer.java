package com.prode.config;

import com.prode.entity.Match;
import com.prode.entity.Match.Phase;
import com.prode.entity.User;
import com.prode.repository.MatchRepository;
import com.prode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
        createMatchesIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("admin@prode.com")) {
            User admin = User.builder()
                    .name("Administrador")
                    .email("admin@prode.com")
                    .password(passwordEncoder.encode("admin123"))
                    .admin(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin creado: admin@prode.com / admin123");
        }
    }

    private void createMatchesIfNotExists() {
        if (matchRepository.count() > 0) return;

        List<Match> matches = List.of(
            // ===== FASE DE GRUPOS =====
            // Grupo A
            m("A", "México",     "🇲🇽", "Argentina",  "🇦🇷", "2026-06-11", "18:00"),
            m("A", "Marruecos",  "🇲🇦", "Islandia",   "🇮🇸", "2026-06-11", "21:00"),
            m("A", "Argentina",  "🇦🇷", "Marruecos",  "🇲🇦", "2026-06-15", "15:00"),
            m("A", "Islandia",   "🇮🇸", "México",     "🇲🇽", "2026-06-15", "18:00"),
            m("A", "Argentina",  "🇦🇷", "Islandia",   "🇮🇸", "2026-06-19", "18:00"),
            m("A", "México",     "🇲🇽", "Marruecos",  "🇲🇦", "2026-06-19", "18:00"),
            // Grupo B
            m("B", "España",     "🇪🇸", "Brasil",     "🇧🇷", "2026-06-12", "15:00"),
            m("B", "Japón",      "🇯🇵", "Croacia",    "🇭🇷", "2026-06-12", "18:00"),
            m("B", "Brasil",     "🇧🇷", "Japón",      "🇯🇵", "2026-06-16", "15:00"),
            m("B", "Croacia",    "🇭🇷", "España",     "🇪🇸", "2026-06-16", "18:00"),
            m("B", "Brasil",     "🇧🇷", "Croacia",    "🇭🇷", "2026-06-20", "18:00"),
            m("B", "España",     "🇪🇸", "Japón",      "🇯🇵", "2026-06-20", "18:00"),
            // Grupo C
            m("C", "Francia",    "🇫🇷", "Alemania",   "🇩🇪", "2026-06-12", "21:00"),
            m("C", "Portugal",   "🇵🇹", "Bélgica",    "🇧🇪", "2026-06-13", "15:00"),
            m("C", "Alemania",   "🇩🇪", "Portugal",   "🇵🇹", "2026-06-17", "15:00"),
            m("C", "Bélgica",    "🇧🇪", "Francia",    "🇫🇷", "2026-06-17", "18:00"),
            m("C", "Alemania",   "🇩🇪", "Bélgica",    "🇧🇪", "2026-06-21", "18:00"),
            m("C", "Francia",    "🇫🇷", "Portugal",   "🇵🇹", "2026-06-21", "18:00"),
            // Grupo D
            m("D", "Inglaterra", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "Colombia",   "🇨🇴", "2026-06-13", "18:00"),
            m("D", "Uruguay",    "🇺🇾", "Senegal",    "🇸🇳", "2026-06-13", "21:00"),
            m("D", "Colombia",   "🇨🇴", "Uruguay",    "🇺🇾", "2026-06-17", "21:00"),
            m("D", "Senegal",    "🇸🇳", "Inglaterra", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "2026-06-18", "15:00"),
            m("D", "Colombia",   "🇨🇴", "Senegal",    "🇸🇳", "2026-06-22", "18:00"),
            m("D", "Inglaterra", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "Uruguay",    "🇺🇾", "2026-06-22", "18:00"),
            // Grupo E
            m("E", "Países Bajos","🇳🇱","Ecuador",    "🇪🇨", "2026-06-14", "15:00"),
            m("E", "EEUU",       "🇺🇸", "Túnez",      "🇹🇳", "2026-06-14", "18:00"),
            m("E", "Ecuador",    "🇪🇨", "EEUU",       "🇺🇸", "2026-06-18", "18:00"),
            m("E", "Túnez",      "🇹🇳", "Países Bajos","🇳🇱","2026-06-18", "21:00"),
            m("E", "Ecuador",    "🇪🇨", "Túnez",      "🇹🇳", "2026-06-22", "21:00"),
            m("E", "Países Bajos","🇳🇱","EEUU",       "🇺🇸", "2026-06-22", "21:00"),
            // Grupo F
            m("F", "Italia",     "🇮🇹", "Canadá",     "🇨🇦", "2026-06-14", "21:00"),
            m("F", "Australia",  "🇦🇺", "Arabia S.",  "🇸🇦", "2026-06-15", "15:00"),
            m("F", "Canadá",     "🇨🇦", "Australia",  "🇦🇺", "2026-06-19", "15:00"),
            m("F", "Arabia S.",  "🇸🇦", "Italia",     "🇮🇹", "2026-06-19", "21:00"),
            m("F", "Canadá",     "🇨🇦", "Arabia S.",  "🇸🇦", "2026-06-23", "18:00"),
            m("F", "Italia",     "🇮🇹", "Australia",  "🇦🇺", "2026-06-23", "18:00"),
            // Grupo G
            m("G", "Chile",      "🇨🇱", "Suiza",      "🇨🇭", "2026-06-15", "21:00"),
            m("G", "Camerún",    "🇨🇲", "Dinamarca",  "🇩🇰", "2026-06-16", "15:00"),
            m("G", "Suiza",      "🇨🇭", "Camerún",    "🇨🇲", "2026-06-20", "15:00"),
            m("G", "Dinamarca",  "🇩🇰", "Chile",      "🇨🇱", "2026-06-20", "21:00"),
            m("G", "Suiza",      "🇨🇭", "Dinamarca",  "🇩🇰", "2026-06-24", "18:00"),
            m("G", "Chile",      "🇨🇱", "Camerún",    "🇨🇲", "2026-06-24", "18:00"),
            // Grupo H
            m("H", "Corea del Sur","🇰🇷","Ghana",     "🇬🇭", "2026-06-16", "21:00"),
            m("H", "Irán",       "🇮🇷", "Senegal",    "🇸🇳", "2026-06-17", "21:00"),
            m("H", "Ghana",      "🇬🇭", "Irán",       "🇮🇷", "2026-06-21", "15:00"),
            m("H", "Senegal",    "🇸🇳", "Corea del Sur","🇰🇷","2026-06-21", "21:00"),
            m("H", "Ghana",      "🇬🇭", "Senegal",    "🇸🇳", "2026-06-25", "18:00"),
            m("H", "Corea del Sur","🇰🇷","Irán",      "🇮🇷", "2026-06-25", "18:00"),

            // ===== ELIMINACIÓN DIRECTA (sin equipos definidos aún) =====
            me("R32-1",  Phase.ROUND_OF_32, "1A vs 2B",   "", "Por definir", "", "2026-07-01", "18:00"),
            me("R32-2",  Phase.ROUND_OF_32, "1B vs 2A",   "", "Por definir", "", "2026-07-01", "21:00"),
            me("R32-3",  Phase.ROUND_OF_32, "1C vs 2D",   "", "Por definir", "", "2026-07-02", "18:00"),
            me("R32-4",  Phase.ROUND_OF_32, "1D vs 2C",   "", "Por definir", "", "2026-07-02", "21:00"),
            me("R32-5",  Phase.ROUND_OF_32, "1E vs 2F",   "", "Por definir", "", "2026-07-03", "18:00"),
            me("R32-6",  Phase.ROUND_OF_32, "1F vs 2E",   "", "Por definir", "", "2026-07-03", "21:00"),
            me("R32-7",  Phase.ROUND_OF_32, "1G vs 2H",   "", "Por definir", "", "2026-07-04", "18:00"),
            me("R32-8",  Phase.ROUND_OF_32, "1H vs 2G",   "", "Por definir", "", "2026-07-04", "21:00"),
            me("QF-1",   Phase.QUARTER,     "Clasificado", "", "Por definir", "", "2026-07-09", "18:00"),
            me("QF-2",   Phase.QUARTER,     "Clasificado", "", "Por definir", "", "2026-07-09", "21:00"),
            me("QF-3",   Phase.QUARTER,     "Clasificado", "", "Por definir", "", "2026-07-10", "18:00"),
            me("QF-4",   Phase.QUARTER,     "Clasificado", "", "Por definir", "", "2026-07-10", "21:00"),
            me("SF-1",   Phase.SEMI,        "Clasificado", "", "Por definir", "", "2026-07-14", "21:00"),
            me("SF-2",   Phase.SEMI,        "Clasificado", "", "Por definir", "", "2026-07-15", "21:00"),
            me("FINAL",  Phase.FINAL,       "Clasificado", "", "Por definir", "", "2026-07-19", "20:00")
        );

        matchRepository.saveAll(matches);
        log.info("Cargados {} partidos en la base de datos.", matches.size());
    }

    private Match m(String group, String home, String hFlag, String away, String aFlag, String date, String time) {
        return Match.builder()
                .groupName(group)
                .phase(Phase.GROUP)
                .homeTeam(home).homeFlag(hFlag)
                .awayTeam(away).awayFlag(aFlag)
                .matchDate(LocalDate.parse(date))
                .matchTime(time)
                .build();
    }

    private Match me(String group, Phase phase, String home, String hFlag, String away, String aFlag, String date, String time) {
        return Match.builder()
                .groupName(group)
                .phase(phase)
                .homeTeam(home).homeFlag(hFlag)
                .awayTeam(away).awayFlag(aFlag)
                .matchDate(LocalDate.parse(date))
                .matchTime(time)
                .build();
    }
}
