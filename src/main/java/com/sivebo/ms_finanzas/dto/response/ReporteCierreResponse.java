package com.sivebo.ms_finanzas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReporteCierreResponse {

    private Long idSesion;
    private Long idCaja;
    private Long idUsuario;
    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    private BigDecimal montoApertura;
    private BigDecimal montoCierre;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoCalculado;
    private BigDecimal diferenciaCuadre;
}
