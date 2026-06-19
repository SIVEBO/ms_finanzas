package com.sivebo.ms_finanzas.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCierreResponse {

    private Long idSesion;
    private Long idCaja;
    private BigDecimal montoApertura;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal montoEsperado;
    private BigDecimal montoCierreDeclarado;
    private BigDecimal diferenciaCuadre;
}
