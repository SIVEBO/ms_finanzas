package com.sivebo.ms_finanzas.service;

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import java.util.List;


public interface CajaSucursalService {
    CajaSucursalResponse crear(CajaSucursalRequest request);
    CajaSucursalResponse obtenerPorId(Long id);
    CajaSucursalResponse obtenerPorSucursal(Long idSucursal);
    List<CajaSucursalResponse> listarTodas();
    CajaSucursalResponse actualizarEstado(Long id, String nuevoEstado);
}
