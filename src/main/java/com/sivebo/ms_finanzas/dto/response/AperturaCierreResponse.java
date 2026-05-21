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
    private Long idCaja;
    private Long idUsuario;
    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    private BigDecimal montoApertura;
    private BigDecimal montoCierre;  
}
