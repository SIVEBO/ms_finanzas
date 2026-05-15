package com.sivebo.ms_finanzas.service.impl;

import com.sivebo.ms_finanzas.dto.request.MovimientoCajaRequest;
import com.sivebo.ms_finanzas.dto.response.MovimientoCajaResponse;
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;
import com.sivebo.ms_finanzas.service.MovimientoCajaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoCajaServiceImpl implements MovimientoCajaService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoCajaServiceImpl.class);
    private final MovimientoCajaRepository repository;
    private final AperturaCierreRepository aperturaCierreRepository;

    @Override
    public MovimientoCajaResponse registrar(MovimientoCajaRequest request) {
        log.info("Registrando movimiento tipo: {} en sesión id: {}", request.getTipo(), request.getIdSesion());
        AperturaCierre sesion = aperturaCierreRepository.findById(request.getIdSesion())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        MovimientoCaja mov = new MovimientoCaja();
        mov.setSesion(sesion);
        mov.setTipo(request.getTipo());
        mov.setMonto(request.getMonto());
        mov.setIdReferenciaVta(request.getIdReferenciaVta());
        return toResponse(repository.save(mov));
    }

    @Override
    public List<MovimientoCajaResponse> listarPorSesion(Long idSesion) {
        log.info("Listando movimientos de sesión id: {}", idSesion);
        return repository.findBySesion_IdSesion(idSesion).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<MovimientoCajaResponse> listarPorSesionYTipo(Long idSesion, String tipo) {
        log.info("Listando movimientos tipo: {} de sesión id: {}", tipo, idSesion);
        return repository.findBySesion_IdSesionAndTipo(idSesion, tipo).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private MovimientoCajaResponse toResponse(MovimientoCaja m) {
        MovimientoCajaResponse r = new MovimientoCajaResponse();
        r.setIdMovimiento(m.getIdMovimiento());
        r.setIdSesion(m.getSesion().getIdSesion());
        r.setTipo(m.getTipo());
        r.setMonto(m.getMonto());
        r.setIdReferenciaVta(m.getIdReferenciaVta());
        return r;
    }
}