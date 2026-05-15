package com.sivebo.ms_finanazas.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MovimientoCajaResponse {
    private Long idMovimiento;
    private Long idSesion;
    private String tipo;
    private BigDecimal monto;
    private Long idReferenciaVta;
    
}
