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
            m("A", Phase.F1, "México",     "🇲🇽", "Sudáfrica",  "🇿🇦", "2026-06-11", "16:00"),
            m("A", Phase.F1, "Corea del Sur",  "🇰🇷", "República Checa",   "🇨🇿", "2026-06-11", "23:00"),
            m("A", Phase.F2, "República Checa",  "🇨🇿", "Sudáfrica",  "🇿🇦", "2026-06-18", "13:00"),
            m("A", Phase.F2, "México",   "🇲🇽", "Corea del Sur",     "🇰🇷", "2026-06-18", "22:00"),
            m("A", Phase.F3, "República Checa",  "🇨🇿", "México",   "🇲🇽", "2026-06-24", "22:00"),
            m("A", Phase.F3, "Sudáfrica",     "🇿🇦", "Corea del Sur",  "🇰🇷", "2026-06-24", "22:00"),
            
            // Grupo B
            m("B", Phase.F1, "Canadá",                  "🇨🇦", "Bosnia y Herzegovina", "🇧🇦", "2026-06-12", "16:00"),
            m("B", Phase.F1, "Qatar",                   "🇶🇦", "Suiza",                 "🇨🇭", "2026-06-13", "16:00"),
            m("B", Phase.F2, "Canadá",                  "🇨🇦", "Qatar",                 "🇶🇦", "2026-06-17", "16:00"),
            m("B", Phase.F2, "Bosnia y Herzegovina",    "🇧🇦", "Suiza",                 "🇨🇭", "2026-06-17", "19:00"),
            m("B", Phase.F3, "Suiza",                  "🇨🇭", "Canadá",                 "🇨🇦", "2026-06-22", "16:00"),
            m("B", Phase.F3, "Bosnia y Herzegovina",    "🇧🇦", "Qatar",                 "🇶🇦", "2026-06-22", "16:00"),

            // Grupo C
            m("C", Phase.F1, "Brasil",    "🇧🇷", "Marruecos", "🇲🇦", "2026-06-13", "19:00"),
            m("C", Phase.F1, "Haití",     "🇭🇹", "Escocia",   "🏴", "2026-06-13", "22:00"),
            m("C", Phase.F2, "Brasil",    "🇧🇷", "Haití",     "🇭🇹", "2026-06-18", "19:00"),
            m("C", Phase.F2, "Marruecos", "🇲🇦", "Escocia",   "🏴", "2026-06-18", "22:00"),
            m("C", Phase.F3, "Escocia",    "🏴", "Brasil",   "🇧🇷 ", "2026-06-24", "19:00"),
            m("C", Phase.F3, "Marruecos", "🇲🇦", "Haití",     "🇭🇹", "2026-06-24", "19:00"),

            // Grupo D
            m("D", Phase.F1, "EE.UU.",    "🇺🇸", "Paraguay",  "🇵🇾", "2026-06-14", "16:00"),
            m("D", Phase.F1, "Australia", "🇦🇺", "Turquía",   "🇹🇷", "2026-06-14", "19:00"),
            m("D", Phase.F2, "EE.UU.",    "🇺🇸", "Australia", "🇦🇺", "2026-06-19", "16:00"),
            m("D", Phase.F2, "Paraguay",  "🇵🇾", "Turquía",   "🇹🇷", "2026-06-19", "19:00"),
            m("D", Phase.F3, "Turquía",    "🇹🇷", "EE.UU.",   "🇺🇸", "2026-06-25", "23:00"),
            m("D", Phase.F3, "Paraguay",  "🇵🇾", "Australia", "🇦🇺", "2026-06-25", "23:00"),

            // Grupo E
            m("E", Phase.F1, "Alemania",         "🇩🇪", "Curazao",          "🇨🇼", "2026-06-15", "16:00"),
            m("E", Phase.F1, "Costa de Marfil", "🇨🇮", "Ecuador",          "🇪🇨", "2026-06-15", "19:00"),
            m("E", Phase.F2, "Alemania",         "🇩🇪", "Costa de Marfil", "🇨🇮", "2026-06-20", "16:00"),
            m("E", Phase.F2, "Curazao",          "🇨🇼", "Ecuador",          "🇪🇨", "2026-06-20", "19:00"),
            m("E", Phase.F3, "Ecuador",         "🇪🇨", "Alemania",          "🇩🇪", "2026-06-25", "17:00"),
            m("E", Phase.F3, "Curazao",  "🇨🇼", "Costa de Marfil", "🇨🇮", "2026-06-25", "17:00"),            

            // Grupo F
            m("F", Phase.F1, "Países Bajos", "🇳🇱", "Japón",   "🇯🇵", "2026-06-16", "16:00"),
            m("F", Phase.F1, "Suecia",       "🇸🇪", "Túnez",   "🇹🇳", "2026-06-16", "19:00"),
            m("F", Phase.F2, "Países Bajos", "🇳🇱", "Suecia",  "🇸🇪", "2026-06-21", "16:00"),
            m("F", Phase.F2, "Japón",        "🇯🇵", "Túnez",   "🇹🇳", "2026-06-21", "19:00"),
            m("F", Phase.F3, "Túnez", "🇹🇳", "Países Bajos",   "🇳🇱", "2026-06-26", "20:00"),
            m("F", Phase.F3, "Japón",        "🇯🇵", "Suecia",  "🇸🇪", "2026-06-26", "20:00"),

            // Grupo G
            m("G", Phase.F1, "Bélgica",       "🇧🇪", "Egipto",        "🇪🇬", "2026-06-17", "16:00"),
            m("G", Phase.F1, "Irán",          "🇮🇷", "Nueva Zelanda", "🇳🇿", "2026-06-17", "19:00"),
            m("G", Phase.F2, "Bélgica",       "🇧🇪", "Irán",          "🇮🇷", "2026-06-22", "16:00"),
            m("G", Phase.F2, "Egipto",        "🇪🇬", "Nueva Zelanda", "🇳🇿", "2026-06-22", "19:00"),
            m("G", Phase.F3, "Nueva Zelanda",       "🇳🇿", "Bélgica", "🇧🇪", "2026-06-27", "16:00"),
            m("G", Phase.F3, "Egipto",        "🇪🇬", "Irán",          "🇮🇷", "2026-06-27", "16:00"),

            // Grupo H
            m("H", Phase.F1, "España",          "🇪🇸", "Cabo Verde",      "🇨🇻", "2026-06-18", "16:00"),
            m("H", Phase.F1, "Arabia Saudita",  "🇸🇦", "Uruguay",         "🇺🇾", "2026-06-18", "19:00"),
            m("H", Phase.F2, "España",          "🇪🇸", "Arabia Saudita",  "🇸🇦", "2026-06-23", "16:00"),
            m("H", Phase.F2, "Cabo Verde",      "🇨🇻", "Uruguay",         "🇺🇾", "2026-06-23", "19:00"),
            m("H", Phase.F3, "Uruguay",          "🇺🇾", "España",         "🇪🇸", "2026-06-28", "16:00"),
            m("H", Phase.F3, "Cabo Verde",      "🇨🇻", "Arabia Saudita",  "🇸🇦", "2026-06-28", "16:00"),

            // Grupo I
            m("I", Phase.F1, "Francia",  "🇫🇷", "Senegal", "🇸🇳", "2026-06-19", "16:00"),
            m("I", Phase.F1, "Irak",     "🇮🇶", "Noruega", "🇳🇴", "2026-06-19", "19:00"),
            m("I", Phase.F2, "Francia",  "🇫🇷", "Irak",    "🇮🇶", "2026-06-24", "16:00"),
            m("I", Phase.F2, "Senegal",  "🇸🇳", "Noruega", "🇳🇴", "2026-06-24", "19:00"),
            m("I", Phase.F3, "Francia",  "🇫🇷", "Noruega", "🇳🇴", "2026-06-29", "16:00"),
            m("I", Phase.F3, "Senegal",  "🇸🇳", "Irak",    "🇮🇶", "2026-06-29", "16:00"),

            // Grupo J
            m("J", Phase.F1, "Argentina", "🇦🇷", "Argelia", "🇩🇿", "2026-06-20", "16:00"),
            m("J", Phase.F1, "Austria",   "🇦🇹", "Jordania","🇯🇴", "2026-06-20", "19:00"),
            m("J", Phase.F2, "Argentina", "🇦🇷", "Austria", "🇦🇹", "2026-06-25", "16:00"),
            m("J", Phase.F2, "Argelia",   "🇩🇿", "Jordania","🇯🇴", "2026-06-25", "19:00"),
            m("J", Phase.F3, "Jordania", "🇯🇴", "Argentina","🇦🇷", "2026-06-30", "16:00"),
            m("J", Phase.F3, "Argelia",   "🇩🇿", "Austria", "🇦🇹", "2026-06-30", "16:00"),

            // Grupo K
            m("K", Phase.F1, "Portugal",    "🇵🇹", "Congo",      "🇨🇬", "2026-06-21", "16:00"),
            m("K", Phase.F1, "Uzbekistán",  "🇺🇿", "Colombia",   "🇨🇴", "2026-06-21", "19:00"),
            m("K", Phase.F2, "Portugal",    "🇵🇹", "Uzbekistán", "🇺🇿", "2026-06-26", "16:00"),
            m("K", Phase.F2, "Congo",       "🇨🇬", "Colombia",   "🇨🇴", "2026-06-26", "19:00"),
            m("K", Phase.F3, "Colombia",    "🇨🇴", "Portugal",   "🇵🇹", "2026-07-01", "16:00"),
            m("K", Phase.F3, "Congo",       "🇨🇬", "Uzbekistán", "🇺🇿", "2026-07-01", "16:00"),

            // Grupo L
            m("L", Phase.F1, "Inglaterra", "🏴", "Croacia", "🇭🇷", "2026-06-22", "16:00"),
            m("L", Phase.F1, "Ghana",      "🇬🇭", "Panamá",  "🇵🇦", "2026-06-22", "19:00"),
            m("L", Phase.F2, "Inglaterra", "🏴", "Ghana",   "🇬🇭", "2026-06-27", "16:00"),
            m("L", Phase.F2, "Croacia",    "🇭🇷", "Panamá",  "🇵🇦", "2026-06-27", "19:00"),
            m("L", Phase.F3, "Panamá", "🇵🇦", "Inglaterra",  "🏴", "2026-07-02", "16:00"),
            m("L", Phase.F3, "Croacia",    "🇭🇷", "Ghana",   "🇬🇭", "2026-07-02", "16:00"),

            // ===== ELIMINACIÓN DIRECTA (sin equipos definidos aún) =====
            me("R32-P73",  Phase.ROUND_OF_32, "2A",   "", "2B", "", "2026-06-28", "16:00"),
            me("R32-P74",  Phase.ROUND_OF_32, "1E",   "", "3ABCDF", "", "2026-06-29", "17:30"),
            me("R32-P75",  Phase.ROUND_OF_32, "1F",   "", "2C", "", "2026-06-29", "22:00"),
            me("R32-P76",  Phase.ROUND_OF_32, "1C",   "", "2F", "", "2026-06-29", "14:00"),
            me("R32-P77",  Phase.ROUND_OF_32, "1I",   "", "3CDFGH", "", "2026-06-30", "18:00"),
            me("R32-P78",  Phase.ROUND_OF_32, "2E",   "", "2I", "", "2026-06-30", "14:00"),
            me("R32-P79",  Phase.ROUND_OF_32, "1A",   "", "3CEFHI", "", "2026-06-30", "22:00"),
            me("R32-P80",  Phase.ROUND_OF_32, "1L",   "", "3CHIJK", "", "2026-07-01", "13:00"),
            me("R32-P81",  Phase.ROUND_OF_32, "1D",   "", "3BEFIJ", "", "2026-07-01", "21:00"),
            me("R32-P82",  Phase.ROUND_OF_32, "1G",   "", "3AEHIJ", "", "2026-07-01", "17:00"),
            me("R32-P83",  Phase.ROUND_OF_32, "2K",   "", "2L", "", "2026-07-02", "20:00"),
            me("R32-P84",  Phase.ROUND_OF_32, "1H",   "", "2J", "", "2026-07-02", "16:00"),
            me("R32-P85",  Phase.ROUND_OF_32, "1B",   "", "3EFGIJ", "", "2026-07-03", "00:00"),
            me("R32-P86",  Phase.ROUND_OF_32, "1J",   "", "2H", "", "2026-07-03", "19:00"),
            me("R32-P87",  Phase.ROUND_OF_32, "1K",   "", "3DEIJL", "", "2026-07-03", "22:30"),
            me("R32-P88",  Phase.ROUND_OF_32, "2D",   "", "2G", "", "2026-07-03", "15:00"),
            
            me("OF-1-P89",  Phase.ROUND_OF_16, "W74",   "", "W77", "", "2026-07-04", "18:00"),
            me("OF-2-P90",  Phase.ROUND_OF_16, "W73",   "", "W75", "", "2026-07-04", "14:00"),
            me("OF-3-P91",  Phase.ROUND_OF_16, "W76",   "", "W78", "", "2026-07-05", "16:00"),
            me("OF-4-P92",  Phase.ROUND_OF_16, "W79",   "", "W80", "", "2026-07-05", "21:00"),
            me("OF-5-P93",  Phase.ROUND_OF_16, "W83",   "", "W84", "", "2026-07-06", "17:00"),
            me("OF-6-P94",  Phase.ROUND_OF_16, "W81",   "", "W82", "", "2026-07-06", "21:00"),
            me("OF-7-P95",  Phase.ROUND_OF_16, "W86",   "", "W88", "", "2026-07-07", "13:00"),
            me("OF-8-P96",  Phase.ROUND_OF_16, "W85",   "", "W87", "", "2026-07-07", "17:00"),

            me("QF-1-P97",   Phase.QUARTER,     "W89", "", "W90", "", "2026-07-09", "17:00"),
            me("QF-2-P98",   Phase.QUARTER,     "W93", "", "W94", "", "2026-07-10", "16:00"),
            me("QF-3-P99",   Phase.QUARTER,     "W91", "", "W92", "", "2026-07-11", "18:00"),
            me("QF-4-P100",  Phase.QUARTER,     "W95", "", "W96", "", "2026-07-11", "22:00"),
            me("SF-1-P101",  Phase.SEMI,        "W97", "", "W98", "", "2026-07-14", "16:00"),
            me("SF-2-P102",  Phase.SEMI,        "W99", "", "W100", "", "2026-07-15", "16:00"),
            me("TERCERO",   Phase.TERCERO,  "D101", "", "D102", "", "2026-07-18", "18:00"),
            me("FINAL",  Phase.FINAL,       "W101", "", "W102", "", "2026-07-19", "16:00")
        );

        matchRepository.saveAll(matches);
        log.info("Cargados {} partidos en la base de datos.", matches.size());
    }

    private Match m(String group, Phase phase, String home, String hFlag, String away, String aFlag, String date, String time) {
        return Match.builder()
                .groupName(group)
                .phase(phase)
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
