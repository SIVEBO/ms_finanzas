package com.sivebo.ms_finanzas.service;

import com.sivebo.ms_finanzas.dto.request.AperturaCierreRequest;
import com.sivebo.ms_finanzas.dto.response.AperturaCierreResponse;
import java.math.BigDecimal;
import java.util.List;

public interface AperturaCierreService {
    AperturaCierreResponse abrirCaja(AperturaCierreRequest request);
    AperturaCierreResponse cerrarCaja(Long idSesion, BigDecimal montoCierre);
    AperturaCierreResponse obtenerPorId(Long id);
    List<AperturaCierreResponse> listarPorCaja(Long idCaja);
    AperturaCierreResponse obtenerSesionAbierta(Long idCaja);    
    
}
