package com.prode.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="Fecha_Tope_Predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FechaTopePrediction {

    @Id
    @Column(name="phase", nullable = false)
    private String phase;

    @Column(name = "fecha_tope_prediction", nullable = false)
    private LocalDateTime fechaTopePrediction;

}
