package com.sivebo.ms_finanzas.dto.request;

import java.math.BigDecimal;

import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaRequest {
    
    @NotNull(message = "El id de la sesión es obligatorio")
    private Long idSesion;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    @NotNull(message = "El monto del movimiento es obligatorio")
    @Positive(message = "El monto del movimiento debe ser positivo")
    private BigDecimal monto;

    private Long idVenta;

    private String concepto;
}
