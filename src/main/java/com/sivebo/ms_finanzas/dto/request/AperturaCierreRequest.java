package com.sivebo.ms_finanzas.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AperturaCierreRequest {
    @NotNull(message = "El id de la caja es obligatorio")
    private Long idCaja;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El monto de apertura es obligatorio")
    @Positive(message = "El monto de apertura debe ser positivo")
    private BigDecimal montoApertura;    
}
