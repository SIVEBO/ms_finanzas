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
public class AperturaCierreRequest {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String nombreSucursal;

    @NotBlank(message = "El username del usuario es obligatorio")
    private String username;

    @NotNull(message = "El monto de apertura es obligatorio")
    @Positive(message = "El monto de apertura debe ser positivo")
    private BigDecimal montoApertura;    
}
