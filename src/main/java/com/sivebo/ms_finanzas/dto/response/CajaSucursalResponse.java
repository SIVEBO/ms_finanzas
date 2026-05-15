package com.sivebo.ms_finanzas.dto.response;

import lombok.Data;

@Data
public class CajaSucursalResponse {
    private Long idCaja;
    private Long idSucursal;
    private String estadoActual;
}