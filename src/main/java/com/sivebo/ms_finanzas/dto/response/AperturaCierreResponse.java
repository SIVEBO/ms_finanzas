package com.sivebo.ms_finanzas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AperturaCierreResponse {
    
    private Long idSesion;
    private String codSesion;
    private String nombreSucursal;
    private String username;
    private LocalDateTime fechaHoraAp;
    private LocalDateTime fechaHoraCi;
    private BigDecimal montoApertura;
    private BigDecimal montoCierre;
    private BigDecimal saldoCalculado;
    private BigDecimal diferenciaCuadre;
}
