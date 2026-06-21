package com.sivebo.ms_finanzas.dto.response;

import java.math.BigDecimal;
<<<<<<< HEAD

import lombok.AllArgsConstructor;
=======
import java.time.LocalDateTime;

>>>>>>> refactor
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
<<<<<<< HEAD
@AllArgsConstructor
=======
>>>>>>> refactor
public class ReporteCierreResponse {

    private Long idSesion;
    private Long idCaja;
<<<<<<< HEAD
    private BigDecimal montoApertura;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal montoEsperado;
    private BigDecimal montoCierreDeclarado;
=======
    private Long idUsuario;
    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    private BigDecimal montoApertura;
    private BigDecimal montoCierre;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoCalculado;
>>>>>>> refactor
    private BigDecimal diferenciaCuadre;
}
