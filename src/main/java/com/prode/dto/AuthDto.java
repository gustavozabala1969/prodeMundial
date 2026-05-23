package com.prode.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

// ---- AUTH ----

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "El nombre es requerido")
        private String name;

        @Email(message = "Email inválido")
        @NotBlank(message = "El email es requerido")
        private String email;

        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private Long userId;
        private String name;
        private String email;
        private boolean admin;

        public AuthResponse(String token, Long userId, String name, String email, boolean admin) {
            this.token = token;
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.admin = admin;
        }
    }
}

// ---- MATCH ----

class MatchDto {

    @Data
    public static class MatchResponse {
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
}

// ---- PREDICTION ----

class PredictionDto {

    @Data
    public static class SaveRequest {
        @NotNull
        private Long matchId;

        @Min(0) @Max(30)
        @NotNull
        private Integer homeScore;

        @Min(0) @Max(30)
        @NotNull
        private Integer awayScore;
    }

    @Data
    public static class BulkSaveRequest {
        private List<SaveRequest> predictions;
    }
}

// ---- RANKING ----

class RankingDto {

    @Data
    public static class RankingEntry {
        private int position;
        private Long userId;
        private String name;
        private String email;
        private long totalPoints;
        private long predCount;
        private long exactCount;
        private long resultCount;
    }
}

// ---- ADMIN ----

class AdminDto {

    @Data
    public static class SetResultRequest {
        @NotNull
        private Long matchId;

        @Min(0) @Max(30)
        @NotNull
        private Integer homeScore;

        @Min(0) @Max(30)
        @NotNull
        private Integer awayScore;
    }
}

// ---- COMPARACION ----

class CompareDto {

    @Data
    public static class CompareEntry {
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
    }
}
