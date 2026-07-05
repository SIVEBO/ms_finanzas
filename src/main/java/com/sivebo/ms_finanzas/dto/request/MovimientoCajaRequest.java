package com.sivebo.ms_finanzas.dto.request;

import java.math.BigDecimal;

import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaRequest {

    @NotBlank(message = "El código de sesión es obligatorio")
    private String codSesion;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    @NotNull(message = "El monto del movimiento es obligatorio")
    @Positive(message = "El monto del movimiento debe ser positivo")
    private BigDecimal monto;

    private String nroBoleta;

    private String concepto;
}
