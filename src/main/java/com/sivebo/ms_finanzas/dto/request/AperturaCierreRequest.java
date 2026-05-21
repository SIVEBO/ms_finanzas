package com.sivebo.ms_finanzas.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AperturaCierreRequest {
    
    @NotNull(message = "El id de la caja es obligatorio")
    private Long idCaja;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El monto de apertura es obligatorio")
    @Positive(message = "El monto de apertura debe ser positivo")
    private BigDecimal montoApertura;    
}
