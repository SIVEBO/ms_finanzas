package com.sivebo.ms_finanzas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.dto.request.MovimientoCajaRequest;
import com.sivebo.ms_finanzas.dto.response.MovimientoCajaResponse;
import com.sivebo.ms_finanzas.exception.RecursoNoEncontradoException;
import com.sivebo.ms_finanzas.exception.ReglaNegocioException;
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientoCajaService {


    private final MovimientoCajaRepository repository;
    private final AperturaCierreRepository aperturaCierreRepository;

    
    public MovimientoCajaResponse registrar(MovimientoCajaRequest request) {
        log.info("Registrando movimiento tipo: {} en sesión id: {}", request.getTipo(), request.getIdSesion());
        AperturaCierre sesion = aperturaCierreRepository.findById(request.getIdSesion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada"));

        if (request.getTipo() == TipoMovimiento.EGRESO
                && request.getIdReferenciaVta() == null
                && (request.getConcepto() == null || request.getConcepto().isBlank())) {
            throw new ReglaNegocioException("El concepto es obligatorio para egresos manuales");
        }

        MovimientoCaja mov = new MovimientoCaja();
        mov.setSesion(sesion);
        mov.setTipo(request.getTipo());
        mov.setMonto(request.getMonto());
        mov.setIdReferenciaVta(request.getIdReferenciaVta());
        mov.setConcepto(request.getConcepto());
        return toResponse(repository.save(mov));
    }

    public List<MovimientoCajaResponse> listarPorSesion(Long idSesion) {
        log.info("Listando movimientos de sesión id: {}", idSesion);
        return repository.findBySesionIdSesion(idSesion).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MovimientoCajaResponse> listarPorSesionYTipo(Long idSesion, TipoMovimiento tipo) {
        log.info("Listando movimientos tipo: {} de sesión id: {}", tipo, idSesion);
        return repository.findBySesionIdSesionAndTipo(idSesion, tipo).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private MovimientoCajaResponse toResponse(MovimientoCaja m) {
        MovimientoCajaResponse r = new MovimientoCajaResponse();
        r.setIdMovimiento(m.getIdMovimiento());
        r.setIdSesion(m.getSesion().getIdSesion());
        r.setTipo(m.getTipo().name());
        r.setMonto(m.getMonto());
        r.setIdReferenciaVta(m.getIdReferenciaVta());
        r.setConcepto(m.getConcepto());
        return r;
    }
}