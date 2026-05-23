package com.prode.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;

@Data
public class SetResultRequest {
    @NotNull private Long matchId;
    @Min(0) @Max(30) @NotNull private Integer homeScore;
    @Min(0) @Max(30) @NotNull private Integer awayScore;
}
