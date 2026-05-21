package com.sivebo.ms_finanzas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CajaSucursalResponse {
    
    private Long idCaja;
    private Long idSucursal;
    private String estadoActual;
}