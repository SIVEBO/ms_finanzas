package com.sivebo.ms_finanzas.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CajaSucursalRequest {

    @NotNull(message = "El id de la sucursal es obligatorio")
    private Long idSucursal;

    @NotNull(message = "El estado actual es obligatorio")
    private String estadoActual;
}