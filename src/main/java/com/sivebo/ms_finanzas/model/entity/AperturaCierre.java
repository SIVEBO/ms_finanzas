package com.sivebo.ms_finanzas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apertura_cierre", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cod_sesion"})
})
public class AperturaCierre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long idSesion;

    @Column(name = "cod_sesion", nullable = false, unique = true)
    private String codSesion;

    @Column(name = "nombre_sucursal", nullable = false)
    private String nombreSucursal;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "monto_apertura", nullable = false)
    private BigDecimal montoApertura;

    @Column(name = "monto_cierre")
    private BigDecimal montoCierre;

    @Column(name = "fecha_hora_ap", nullable = false)
    private LocalDateTime fechaHoraAp;

    @Column(name = "fecha_hora_ci")
    private LocalDateTime fechaHoraCi;
}
