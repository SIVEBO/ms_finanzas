package com.sivebo.ms_finanzas.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "movimiento_caja")
public class MovimientoCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    @Column(name = "id_sesion", nullable = false)
    private AperturaCierre sesion;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "id_referencia_vta")
    private Long idReferenciaVta;
}
