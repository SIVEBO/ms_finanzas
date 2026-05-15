package com.sivebo.ms_finanzas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.dto.request.AperturaCierreRequest;
import com.sivebo.ms_finanzas.dto.response.AperturaCierreResponse;
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;
import com.sivebo.ms_finanzas.service.AperturaCierreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AperturaCierreServiceImpl implements AperturaCierreService {

    private static final Logger log = LoggerFactory.getLogger(AperturaCierreServiceImpl.class);
    private final AperturaCierreRepository repository;
    private final CajaSucursalRepository cajaRepository;

    @Override
    public AperturaCierreResponse abrirCaja(AperturaCierreRequest request) {
        log.info("Abriendo caja id: {}", request.getIdCaja());
        CajaSucursal caja = cajaRepository.findById(request.getIdCaja())
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
        caja.setEstadoActual("ABIERTA");
        cajaRepository.save(caja);

        AperturaCierre apertura = new AperturaCierre();
        apertura.setCaja(caja);
        apertura.setIdUsuario(request.getIdUsuario());
        apertura.setMontoApertura(request.getMontoApertura());
        apertura.setFechaHoraApertura(LocalDateTime.now());
        return toResponse(repository.save(apertura));
    }

    @Override
    public AperturaCierreResponse cerrarCaja(Long idSesion, BigDecimal montoCierre) {
        log.info("Cerrando sesión id: {}", idSesion);
        AperturaCierre sesion = repository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        sesion.setMontoCierre(montoCierre);
        sesion.setFechaHoraCierre(LocalDateTime.now());
        sesion.getCaja().setEstadoActual("CERRADA");
        cajaRepository.save(sesion.getCaja());
        return toResponse(repository.save(sesion));
    }

    @Override
    public AperturaCierreResponse obtenerPorId(Long id) {
        log.info("Buscando sesión id: {}", id);
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada")));
    }

    @Override
    public List<AperturaCierreResponse> listarPorCaja(Long idCaja) {
        log.info("Listando sesiones de caja id: {}", idCaja);
        return repository.findByCaja_IdCaja(idCaja).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public AperturaCierreResponse obtenerSesionAbierta(Long idCaja) {
        log.info("Buscando sesión abierta de caja id: {}", idCaja);
        return toResponse(repository.findByCaja_IdCajaAndFechaHoraCierreIsNull(idCaja)
                .orElseThrow(() -> new RuntimeException("No hay sesión abierta para esta caja")));
    }

    private AperturaCierreResponse toResponse(AperturaCierre a) {
        AperturaCierreResponse r = new AperturaCierreResponse();
        r.setIdSesion(a.getIdSesion());
        r.setIdCaja(a.getCaja().getIdCaja());
        r.setIdUsuario(a.getIdUsuario());
        r.setMontoApertura(a.getMontoApertura());
        r.setMontoCierre(a.getMontoCierre());
        r.setFechaHoraApertura(a.getFechaHoraApertura());
        r.setFechaHoraCierre(a.getFechaHoraCierre());
        return r;
    }
}
