package com.sivebo.ms_finanzas.service;

import java.util.List;

import com.sivebo.ms_finanzas.dto.request.MovimientoCajaRequest;
import com.sivebo.ms_finanzas.dto.response.MovimientoCajaResponse;

public interface MovimientoCajaService {
    MovimientoCajaResponse registrar(MovimientoCajaRequest request);
    List<MovimientoCajaResponse> listarPorSesion(Long idSesion);
    List<MovimientoCajaResponse> listarPorSesionYTipo(Long idSesion, String tipo);
}