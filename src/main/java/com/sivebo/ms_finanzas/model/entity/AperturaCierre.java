package com.sivebo.ms_finanzas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apertura_cierre")
public class AperturaCierre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long idSesion;

    @ManyToOne
    @JoinColumn(name = "id_caja", nullable = false)
    private CajaSucursal caja;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "monto_apertura", nullable = false)
    private BigDecimal montoApertura;

    @Column(name = "monto_cierre")
    private BigDecimal montoCierre;

    @Column(name = "fecha_hora_apertura", nullable = false)
    private LocalDateTime fechaHoraApertura;

    @Column(name = "fecha_hora_cierre")
    private LocalDateTime fechaHoraCierre;
}