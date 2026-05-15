package com.sivebo.ms_finanzas.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AperturaCierreResponse {
    private Long idSesion;
    private Long idCaja;
    private Long idUsuario;
    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    private BigDecimal montoApertura;
    private BigDecimal montoCierre;  
}
