package com.sivebo.ms_finanzas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
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
