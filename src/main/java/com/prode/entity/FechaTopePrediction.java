package com.prode.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Fecha_Tope_Predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FechaTopePrediction {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false)
    private Match.Phase phase;

    @Column(name = "fecha_tope_prediction", nullable = false)
    private LocalDateTime fechaTopePrediction;

}
