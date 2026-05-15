package com.sivebo.ms_finanzas.service.impl;

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;
import com.sivebo.ms_finanzas.service.CajaSucursalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CajaSucursalServiceImpl implements CajaSucursalService {

    private static final Logger log = LoggerFactory.getLogger(CajaSucursalServiceImpl.class);
    private final CajaSucursalRepository repository;

    @Override
    public CajaSucursalResponse crear(CajaSucursalRequest request) {
        log.info("Creando caja para sucursal id: {}", request.getIdSucursal());
        CajaSucursal caja = new CajaSucursal();
        caja.setIdSucursal(request.getIdSucursal());
        caja.setEstadoActual(request.getEstadoActual());
        return toResponse(repository.save(caja));
    }

    @Override
    public CajaSucursalResponse obtenerPorId(Long id) {
        log.info("Buscando caja id: {}", id);
        CajaSucursal caja = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + id));
        return toResponse(caja);
    }

    @Override
    public CajaSucursalResponse obtenerPorSucursal(Long idSucursal) {
        log.info("Buscando caja de sucursal id: {}", idSucursal);
        CajaSucursal caja = repository.findByIdSucursal(idSucursal)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada para sucursal: " + idSucursal));
        return toResponse(caja);
    }

    @Override
    public List<CajaSucursalResponse> listarTodas() {
        log.info("Listando todas las cajas");
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
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
