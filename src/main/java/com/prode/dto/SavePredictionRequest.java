package com.prode.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

@Data
public class SavePredictionRequest {
    @NotNull
    private Long matchId;

    @Min(0) @Max(30) @NotNull
    private Integer homeScore;

    @Min(0) @Max(30) @NotNull
    private Integer awayScore;
}
