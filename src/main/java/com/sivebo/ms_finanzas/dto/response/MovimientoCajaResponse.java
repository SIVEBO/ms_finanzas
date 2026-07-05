package com.sivebo.ms_finanzas.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaResponse {
    
    private Long idMov;
    private String codSesion;
    private String tipo;
    private BigDecimal monto;
    private String nroBoleta;
    private String concepto;

}
