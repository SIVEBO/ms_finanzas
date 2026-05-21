package com.sivebo.ms_finanzas.dto.request;

import java.math.BigDecimal;

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
    
    @NotNull(message = "El id de la sesión es obligatorio")
    private Long idSesion;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipo;

    @NotNull(message = "El monto del movimiento es obligatorio")
    @Positive(message = "El monto del movimiento debe ser positivo")
    private BigDecimal monto;

    private Long idReferenciaVta;
}
