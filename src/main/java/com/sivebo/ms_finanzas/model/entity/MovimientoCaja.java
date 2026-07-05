package com.sivebo.ms_finanzas.model.entity;

import java.math.BigDecimal;

import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento_caja")
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mov")
    private Long idMov;

    @Column(name = "cod_sesion", nullable = false)
    private String codSesion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoMovimiento tipo;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "nro_boleta")
    private String nroBoleta;

    @Column(name = "concepto", length = 255)
    private String concepto;
}
