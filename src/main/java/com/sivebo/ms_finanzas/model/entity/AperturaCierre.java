package com.sivebo.ms_finanzas.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "apertura_cierre")
public class AperturaCierre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSesion;

    @Column(name = "id_caja", nullable = false)
    private CajaSucursal caja;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_hora_apertura", nullable = false)
    private LocalDateTime fechaHoraApertura;

    @Column(name = "monto_apertura", nullable = false)
    private BigDecimal montoApertura;

    @Column(name = "fecha_hora_cierre")
    private LocalDateTime fechaHoraCierre;

    @Column(name = "monto_cierre")
    private BigDecimal montoCierre;    
}
