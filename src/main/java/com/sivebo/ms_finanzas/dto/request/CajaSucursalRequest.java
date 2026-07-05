package com.sivebo.ms_finanzas.dto.request;

import com.sivebo.ms_finanzas.model.enums.EstadoCaja;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CajaSucursalRequest {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String nombreSucursal;

    @NotNull(message = "El estado actual es obligatorio")
    private EstadoCaja estadoActual;
}