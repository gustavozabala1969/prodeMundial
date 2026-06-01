package com.prode.config;

import com.prode.entity.Match;
import com.prode.entity.Match.Phase;
import com.prode.entity.User;
import com.prode.entity.FechaTopePrediction;
import com.prode.repository.MatchRepository;
import com.prode.repository.UserRepository;
import com.prode.repository.FechaTopeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final FechaTopeRepository fechaTopeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
        createMatchesIfNotExists();
        createFechaTopesPredictions();
    }

    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("admin@prode.com")) {
            User admin = User.builder()
                    .name("Administrador")
                    .email("admin@prode.com")
                    .password(passwordEncoder.encode("Gzmundial"))
                    .admin(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin creado: admin@prode.com / Gz.....");
        }
    }

    private void createFechaTopesPredictions() {
        if (fechaTopeRepository.count() > 0) return;

        FechaTopePrediction fechaTopeGroup = FechaTopePrediction.builder()
                .phase("GROUP")
                .fechaTopePrediction(LocalDateTime.parse("2026-06-11T15:50:00"))
                .build();
        fechaTopeRepository.save(fechaTopeGroup);
        log.info("Fecha Tope Predictions GROUP - 2026-06-11 15:50:00");

        FechaTopePrediction fechaTopeF1 = FechaTopePrediction.builder()
                .phase("F1")
                .fechaTopePrediction(LocalDateTime.parse("2026-06-11T15:50:00"))
                .build();
        fechaTopeRepository.save(fechaTopeF1);
        log.info("Fecha Tope Predictions F1 - 2026-06-11 15:50:00");

        FechaTopePrediction fechaTopeF2 = FechaTopePrediction.builder()
                .phase("F2")
                .fechaTopePrediction(LocalDateTime.parse("2026-06-18T12:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeF2);
        log.info("Fecha Tope Predictions F2 - 2026-06-18 12:00:00");

        FechaTopePrediction fechaTopeF3 = FechaTopePrediction.builder()
                .phase("F3")
                .fechaTopePrediction(LocalDateTime.parse("2026-06-24T15:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeF3);
        log.info("Fecha Tope Predictions F3 - 2026-06-24 15:00:00");

        FechaTopePrediction fechaTopeR32 = FechaTopePrediction.builder()
                .phase("ROUND_OF_32")
                .fechaTopePrediction(LocalDateTime.parse("2026-06-28T15:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeR32);
        log.info("Fecha Tope Predictions ROUND_OF_32 - 2026-06-28 15:00:00");

        FechaTopePrediction fechaTopeR16 = FechaTopePrediction.builder()
                .phase("ROUND_OF_16")
                .fechaTopePrediction(LocalDateTime.parse("2026-07-04T13:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeR16);
        log.info("Fecha Tope Predictions ROUND_OF_16 - 2026-07-04 13:00:00");

        FechaTopePrediction fechaTopeR8 = FechaTopePrediction.builder()
                .phase("QUARTER")
                .fechaTopePrediction(LocalDateTime.parse("2026-07-09T16:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeR8);
        log.info("Fecha Tope Predictions QUARTER - 2026-07-09 16:00:00");

        FechaTopePrediction fechaTopeR4 = FechaTopePrediction.builder()
                .phase("SEMI")
                .fechaTopePrediction(LocalDateTime.parse("2026-07-14T15:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeR4);
        log.info("Fecha Tope Predictions SEMI - 2026-07-14 15:00:00");

        FechaTopePrediction fechaTopeTercero = FechaTopePrediction.builder()
                .phase("TERCERO")
                .fechaTopePrediction(LocalDateTime.parse("2026-07-18T17:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeTercero);
        log.info("Fecha Tope Predictions TERCERO - 2026-07-18 17:00:00");

        FechaTopePrediction fechaTopeFinal = FechaTopePrediction.builder()
                .phase("FINAL")
                .fechaTopePrediction(LocalDateTime.parse("2026-07-19T15:00:00"))
                .build();
        fechaTopeRepository.save(fechaTopeFinal);
        log.info("Fecha Tope Predictions FINAL - 2026-07-19 15:00:00");

    }

    private void createMatchesIfNotExists() {
        if (matchRepository.count() > 0) return;

        List<Match> matches = List.of(
            // ===== FASE DE GRUPOS =====
            // Grupo A
            m("A", Phase.F1, "México",     "mx", "Sudáfrica",  "za", "2026-06-11", "16:00"),
            m("A", Phase.F1, "Corea del Sur",  "kr", "República Checa",   "cz", "2026-06-11", "23:00"),
            m("A", Phase.F2, "República Checa",  "cz", "Sudáfrica",  "za", "2026-06-18", "13:00"),
            m("A", Phase.F2, "México",   "mx", "Corea del Sur",     "kr", "2026-06-18", "22:00"),
            m("A", Phase.F3, "República Checa",  "cz", "México",   "mx", "2026-06-24", "22:00"),
            m("A", Phase.F3, "Sudáfrica",     "za", "Corea del Sur",  "kr", "2026-06-24", "22:00"),
            
            // Grupo B
            m("B", Phase.F1, "Canadá",  "ca", "Bosnia y Herzegovina", "ba", "2026-06-12", "16:00"),
            m("B", Phase.F1, "Qatar",   "qa", "Suiza",       "ch", "2026-06-13", "16:00"),
            m("B", Phase.F2, "Suiza",    "ch", "Bosnia y Herzegovina", "ba", "2026-06-18", "16:00"),
            m("B", Phase.F2, "Canadá",  "ca", "Qatar",       "qa", "2026-06-18", "19:00"),
            m("B", Phase.F3, "Suiza",   "ch", "Canadá",  "ca", "2026-06-22", "16:00"),
            m("B", Phase.F3, "Bosnia y Herzegovina",    "ba", "Qatar", "qa", "2026-06-22", "16:00"),

            // Grupo C
            m("C", Phase.F1, "Brasil",    "br", "Marruecos", "ma", "2026-06-13", "19:00"),
            m("C", Phase.F1, "Haití",     "ht", "Escocia",   "gb-sct", "2026-06-13", "22:00"),
            m("C", Phase.F2, "Escocia", "gb-sct", "Marruecos",   "ma", "2026-06-19", "19:00"),
            m("C", Phase.F2, "Brasil",    "br", "Haití",     "ht", "2026-06-19", "21:30"),
            m("C", Phase.F3, "Escocia",    "gb-sct", "Brasil",   "br", "2026-06-24", "19:00"),
            m("C", Phase.F3, "Marruecos", "ma", "Haití",     "ht", "2026-06-24", "19:00"),

            // Grupo D
            m("D", Phase.F1, "EE.UU.",    "us", "Paraguay",  "py", "2026-06-12", "22:00"),
            m("D", Phase.F1, "Australia", "au", "Turquía",   "tr", "2026-06-14", "01:00"),
            m("D", Phase.F2, "EE.UU.",    "us", "Australia", "au", "2026-06-19", "16:00"),
            m("D", Phase.F2, "Turquía",  "tr", "Paraguay",   "py", "2026-06-20", "00:00"),
            m("D", Phase.F3, "Turquía",    "tr", "EE.UU.",   "us", "2026-06-25", "23:00"),
            m("D", Phase.F3, "Paraguay",  "py", "Australia", "au", "2026-06-25", "23:00"),

            // Grupo E
            m("E", Phase.F1, "Alemania",         "de", "Curazao",     "cw", "2026-06-14", "14:00"),
            m("E", Phase.F1, "Costa de Marfil", "ci", "Ecuador",      "ec", "2026-06-15", "20:00"),
            m("E", Phase.F2, "Alemania",         "de", "Costa de Marfil", "ci", "2026-06-20", "17:00"),
            m("E", Phase.F2, "Ecuador",          "ec", "Curazao",          "cw", "2026-06-20", "21:00"),
            m("E", Phase.F3, "Curazao",  "cw", "Costa de Marfil", "ci", "2026-06-25", "17:00"),            
            m("E", Phase.F3, "Ecuador",         "ec", "Alemania", "de", "2026-06-25", "17:00"),
            
            // Grupo F
            m("F", Phase.F1, "Países Bajos", "nl", "Japón",   "jp", "2026-06-14", "17:00"),
            m("F", Phase.F1, "Suecia",       "se", "Túnez",   "tn", "2026-06-16", "23:00"),
            m("F", Phase.F2, "Países Bajos", "nl", "Suecia",  "se", "2026-06-20", "14:00"),
            m("F", Phase.F2, "Túnez",        "tn", "Japón",   "jp", "2026-06-21", "01:00"),
            m("F", Phase.F3, "Japón",        "jp", "Suecia",  "se", "2026-06-25", "20:00"),
            m("F", Phase.F3, "Túnez", "tn", "Países Bajos",   "nl", "2026-06-25", "20:00"),
            
            // Grupo G
            m("G", Phase.F1, "Bélgica",       "be", "Egipto",        "eg", "2026-06-15", "16:00"),
            m("G", Phase.F1, "Irán",          "ir", "Nueva Zelanda", "nz", "2026-06-15", "22:00"),
            m("G", Phase.F2, "Bélgica",       "be", "Irán",          "ir", "2026-06-21", "16:00"),
            m("G", Phase.F2, "Nueva Zelanda",        "nz", "Egipto", "eg", "2026-06-21", "22:00"),
            m("G", Phase.F3, "Egipto",        "eg", "Irán",          "ir", "2026-06-27", "00:00"),
            m("G", Phase.F3, "Nueva Zelanda",       "nz", "Bélgica", "be", "2026-06-27", "00:00"),
            
            // Grupo H
            m("H", Phase.F1, "España",          "es", "Cabo Verde",      "cv", "2026-06-15", "13:00"),
            m("H", Phase.F1, "Arabia Saudita",  "sa", "Uruguay",         "uy", "2026-06-15", "19:00"),
            m("H", Phase.F2, "España",          "es", "Arabia Saudita",  "sa", "2026-06-21", "13:00"),
            m("H", Phase.F2, "Uruguay",      "uy", "Cabo Verde",         "cv", "2026-06-21", "19:00"),
            m("H", Phase.F3, "Cabo Verde",      "cv", "Arabia Saudita",  "sa", "2026-06-26", "21:00"),
            m("H", Phase.F3, "Uruguay",          "uy", "España",         "es", "2026-06-26", "21:00"),
            
            // Grupo I
            m("I", Phase.F1, "Francia",  "fr", "Senegal", "sn", "2026-06-16", "16:00"),
            m("I", Phase.F1, "Irak",     "iq", "Noruega", "no", "2026-06-16", "19:00"),
            m("I", Phase.F2, "Francia",  "fr", "Irak",    "iq", "2026-06-22", "18:00"),
            m("I", Phase.F2, "Noruega",  "no", "Senegal", "sn", "2026-06-22", "21:00"),
            m("I", Phase.F3, "Noruega",  "no", "Francia", "fr", "2026-06-26", "16:00"),
            m("I", Phase.F3, "Senegal",  "sn", "Irak",    "iq", "2026-06-26", "16:00"),

            // Grupo J
            m("J", Phase.F1, "Argentina", "ar", "Argelia", "dz", "2026-06-16", "22:00"),
            m("J", Phase.F1, "Austria",   "at", "Jordania","jo", "2026-06-17", "01:00"),
            m("J", Phase.F2, "Argentina", "ar", "Austria", "at", "2026-06-22", "14:00"),
            m("J", Phase.F2, "Jordania",   "jo", "Argelia","dz", "2026-06-23", "00:00"),
            m("J", Phase.F3, "Argelia",   "dz", "Austria", "at", "2026-06-27", "23:00"),
            m("J", Phase.F3, "Jordania", "jo", "Argentina","ar", "2026-06-27", "23:00"),
            
            // Grupo K
            m("K", Phase.F1, "Portugal",    "pt", "Congo",      "cg", "2026-06-17", "14:00"),
            m("K", Phase.F1, "Uzbekistán",  "uz", "Colombia",   "co", "2026-06-17", "23:00"),
            m("K", Phase.F2, "Portugal",    "pt", "Uzbekistán", "uz", "2026-06-23", "14:00"),
            m("K", Phase.F2, "Colombia",       "co", "Congo",   "cg", "2026-06-23", "23:00"),
            m("K", Phase.F3, "Colombia",    "co", "Portugal",   "pt", "2026-06-27", "20:30"),
            m("K", Phase.F3, "Congo",       "cg", "Uzbekistán", "uz", "2026-06-27", "20:30"),

            // Grupo L
            m("L", Phase.F1, "Inglaterra", "fi-gb-eng", "Croacia", "hr", "2026-06-17", "17:00"),
            m("L", Phase.F1, "Ghana",      "gh", "Panamá",  "pa", "2026-06-17", "20:00"),
            m("L", Phase.F2, "Inglaterra", "fi-gb-eng", "Ghana",   "gh", "2026-06-23", "17:00"),
            m("L", Phase.F2, "Panamá",    "pa", "Croacia",  "hr", "2026-06-23", "20:00"),
            m("L", Phase.F3, "Panamá", "pa", "Inglaterra",  "fi-gb-eng", "2026-06-27", "18:00"),
            m("L", Phase.F3, "Croacia",    "hr", "Ghana",   "gh", "2026-06-27", "18:00"),

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
