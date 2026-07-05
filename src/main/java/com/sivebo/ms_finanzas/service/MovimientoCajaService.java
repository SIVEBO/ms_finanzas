package com.sivebo.ms_finanzas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.client.FinanzasClient;
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
    private final FinanzasClient finanzasClient;


    public MovimientoCajaResponse registrar(MovimientoCajaRequest request) {
        log.info("Registrando movimiento tipo: {} en sesión: {}", request.getTipo(), request.getCodSesion());
        AperturaCierre sesion = aperturaCierreRepository.findByCodSesion(request.getCodSesion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada"));

        if (request.getNroBoleta() != null && !finanzasClient.verificarVenta(request.getNroBoleta())) {
            throw new ReglaNegocioException("La venta indicada no existe en el sistema");
        }

        MovimientoCaja mov = new MovimientoCaja();
        mov.setCodSesion(sesion.getCodSesion());
        mov.setTipo(request.getTipo());
        mov.setMonto(request.getMonto());
        mov.setNroBoleta(request.getNroBoleta());
        mov.setConcepto(request.getConcepto());
        return toResponse(repository.save(mov));
    }


    public List<MovimientoCajaResponse> listarPorSesion(String codSesion) {
        log.info("Listando movimientos de sesión: {}", codSesion);
        return repository.findByCodSesion(codSesion).stream().map(this::toResponse).collect(Collectors.toList());
    }


    public List<MovimientoCajaResponse> listarPorSesionYTipo(String codSesion, TipoMovimiento tipo) {
        log.info("Listando movimientos tipo: {} de sesión: {}", tipo, codSesion);
        return repository.findByCodSesionAndTipo(codSesion, tipo).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private MovimientoCajaResponse toResponse(MovimientoCaja m) {
        MovimientoCajaResponse r = new MovimientoCajaResponse();
        r.setIdMov(m.getIdMov());
        r.setCodSesion(m.getCodSesion());
        r.setTipo(m.getTipo().name());
        r.setMonto(m.getMonto());
        r.setNroBoleta(m.getNroBoleta());
        r.setConcepto(m.getConcepto());
        return r;
    }
}