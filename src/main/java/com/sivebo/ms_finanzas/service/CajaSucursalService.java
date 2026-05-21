package com.sivebo.ms_finanzas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CajaSucursalService {

    private final CajaSucursalRepository cajaSucursalRepository;
    private final AperturaCierreRepository aperturaCierreRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;


    private final CajaSucursalRepository repository;

    
    public CajaSucursalResponse crear(CajaSucursalRequest request) {
        log.info("Creando caja para sucursal id: {}", request.getIdSucursal());
        CajaSucursal caja = new CajaSucursal();
        caja.setIdSucursal(request.getIdSucursal());
        caja.setEstadoActual(request.getEstadoActual());
        return toResponse(repository.save(caja));
    }

    
    public CajaSucursalResponse obtenerPorId(Long id) {
        log.info("Buscando caja id: {}", id);
        CajaSucursal caja = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + id));
        return toResponse(caja);
    }

    
    public CajaSucursalResponse obtenerPorSucursal(Long idSucursal) {
        log.info("Buscando caja de sucursal id: {}", idSucursal);
        CajaSucursal caja = repository.findByIdSucursal(idSucursal)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada para sucursal: " + idSucursal));
        return toResponse(caja);
    }

    
    public List<CajaSucursalResponse> listarTodas() {
        log.info("Listando todas las cajas");
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    
    public CajaSucursalResponse actualizarEstado(Long id, String nuevoEstado) {
        log.info("Actualizando estado de caja id: {} a {}", id, nuevoEstado);
        CajaSucursal caja = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + id));
        caja.setEstadoActual(nuevoEstado);
        return toResponse(repository.save(caja));
    }

    private CajaSucursalResponse toResponse(CajaSucursal caja) {
        CajaSucursalResponse response = new CajaSucursalResponse();
        response.setIdCaja(caja.getIdCaja());
        response.setIdSucursal(caja.getIdSucursal());
        response.setEstadoActual(caja.getEstadoActual());
        return response;
    }

    


}
