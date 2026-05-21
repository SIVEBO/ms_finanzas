package com.sivebo.ms_finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@requiredArgsConstructor
public interface CajaSucursalService {

    private final CajaSucursalRepository cajaSucursalRepository;
    private final AperturaCierreRepository aperturaCierreRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    CajaSucursalResponse crear(CajaSucursalRequest request);
    CajaSucursalResponse obtenerPorId(Long id);
    CajaSucursalResponse obtenerPorSucursal(Long idSucursal);
    List<CajaSucursalResponse> listarTodas();
    CajaSucursalResponse actualizarEstado(Long id, String nuevoEstado);
}
