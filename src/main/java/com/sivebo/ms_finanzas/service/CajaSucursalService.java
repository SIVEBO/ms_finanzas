package com.sivebo.ms_finanzas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.exception.RecursoNoEncontradoException;
import com.sivebo.ms_finanzas.exception.ReglaNegocioException;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.model.enums.EstadoCaja;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CajaSucursalService {

    private final CajaSucursalRepository repository;

    
    public CajaSucursalResponse crear(CajaSucursalRequest request) {
        log.info("Creando caja para sucursal: {}", request.getNombreSucursal());
        if (repository.findByNombreSucursal(request.getNombreSucursal()).isPresent()) {
            throw new ReglaNegocioException(
                    "Ya existe una caja para la sucursal: " + request.getNombreSucursal());
        }
        CajaSucursal caja = new CajaSucursal();
        caja.setNombreSucursal(request.getNombreSucursal());
        caja.setEstadoActual(request.getEstadoActual());
        return toResponse(repository.save(caja));
    }


    public CajaSucursalResponse obtenerPorId(Long id) {
        log.info("Buscando caja id: {}", id);
        CajaSucursal caja = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja no encontrada con id: " + id));
        return toResponse(caja);
    }


    public CajaSucursalResponse obtenerPorSucursal(String nombreSucursal) {
        log.info("Buscando caja de sucursal: {}", nombreSucursal);
        CajaSucursal caja = repository.findByNombreSucursal(nombreSucursal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja no encontrada para sucursal: " + nombreSucursal));
        return toResponse(caja);
    }

    
    public List<CajaSucursalResponse> listarTodas() {
        log.info("Listando todas las cajas");
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    
    public CajaSucursalResponse actualizarEstado(Long id, EstadoCaja nuevoEstado) {
        log.info("Actualizando estado de caja id: {} a {}", id, nuevoEstado);
        CajaSucursal caja = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja no encontrada con id: " + id));
        caja.setEstadoActual(nuevoEstado);
        return toResponse(repository.save(caja));
    }

    private CajaSucursalResponse toResponse(CajaSucursal caja) {
        CajaSucursalResponse response = new CajaSucursalResponse();
        response.setIdCaja(caja.getIdCaja());
        response.setNombreSucursal(caja.getNombreSucursal());
        response.setEstadoActual(caja.getEstadoActual().name());
        return response;
    }

    


}
