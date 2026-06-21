package com.sivebo.ms_finanzas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sivebo.ms_finanzas.dto.request.AperturaCierreRequest;
import com.sivebo.ms_finanzas.dto.response.AperturaCierreResponse;
import com.sivebo.ms_finanzas.dto.response.ReporteCierreResponse;
import com.sivebo.ms_finanzas.exception.RecursoNoEncontradoException;
import com.sivebo.ms_finanzas.exception.ReglaNegocioException;
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
import com.sivebo.ms_finanzas.model.enums.EstadoCaja;
import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;

@ExtendWith(MockitoExtension.class)
class AperturaCierreServiceTest {

    @Mock AperturaCierreRepository repository;
    @Mock CajaSucursalRepository cajaRepository;
    @Mock MovimientoCajaRepository movimientoRepository;

    @InjectMocks AperturaCierreService service;

    private static final LocalDateTime APERTURA_DT = LocalDateTime.of(2026, 6, 1, 8, 0);
    private static final LocalDateTime CIERRE_DT = LocalDateTime.of(2026, 6, 1, 18, 0);

    private CajaSucursal cajaAbierta() {
        return new CajaSucursal(1L, 10L, EstadoCaja.ABIERTA);
    }

    private CajaSucursal cajaCerrada() {
        return new CajaSucursal(1L, 10L, EstadoCaja.CERRADA);
    }

    private AperturaCierre sesionAbierta() {
        return new AperturaCierre(1L, cajaAbierta(), 50L, new BigDecimal("100000"), null, APERTURA_DT, null);
    }

    private AperturaCierre sesionCerrada() {
        return new AperturaCierre(1L, cajaAbierta(), 50L, new BigDecimal("100000"), new BigDecimal("150000"),
                APERTURA_DT, CIERRE_DT);
    }

    @Test
    void abrirCaja_cajaExiste_creaYRetornaSesion() {
        AperturaCierreRequest request = new AperturaCierreRequest(1L, 50L, new BigDecimal("100000"));
        AperturaCierre sesionGuardada = sesionAbierta();

        when(cajaRepository.findById(1L)).thenReturn(Optional.of(cajaCerrada()));
        when(cajaRepository.save(any(CajaSucursal.class))).thenReturn(cajaAbierta());
        when(repository.save(any(AperturaCierre.class))).thenReturn(sesionGuardada);

        AperturaCierreResponse result = service.abrirCaja(request);

        assertEquals(1L, result.getIdSesion());
        assertEquals(new BigDecimal("100000"), result.getMontoApertura());
        verify(cajaRepository).save(any(CajaSucursal.class));
        verify(repository).save(any(AperturaCierre.class));
    }

    @Test
    void abrirCaja_cajaNoExiste_lanzaRecursoNoEncontrado() {
        AperturaCierreRequest request = new AperturaCierreRequest(99L, 50L, new BigDecimal("100000"));
        when(cajaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.abrirCaja(request));
        verify(repository, never()).save(any());
    }

    @Test
    void abrirCaja_cajaYaAbierta_lanzaReglaNegocio() {
        AperturaCierreRequest request = new AperturaCierreRequest(1L, 50L, new BigDecimal("100000"));
        when(cajaRepository.findById(1L)).thenReturn(Optional.of(cajaAbierta()));

        assertThrows(ReglaNegocioException.class, () -> service.abrirCaja(request));
        verify(repository, never()).save(any());
    }

    @Test
    void cerrarCaja_calculaCuadreCorrectamente() {
        AperturaCierre sesion = sesionAbierta();
        MovimientoCaja ingreso = new MovimientoCaja(1L, sesion, TipoMovimiento.INGRESO, new BigDecimal("80000"), 101L, null);
        MovimientoCaja egreso = new MovimientoCaja(2L, sesion, TipoMovimiento.EGRESO, new BigDecimal("30000"), 102L, null);
        AperturaCierre sesionCerrada = sesionCerrada();

        when(repository.findById(1L)).thenReturn(Optional.of(sesion));
        when(movimientoRepository.findBySesionIdSesion(1L)).thenReturn(List.of(ingreso, egreso));
        when(cajaRepository.save(any(CajaSucursal.class))).thenReturn(cajaAbierta());
        when(repository.save(any(AperturaCierre.class))).thenReturn(sesionCerrada);

        // saldoCalculado = 100000 + 80000 - 30000 = 150000; diferencia = 150000 - 150000 = 0
        AperturaCierreResponse result = service.cerrarCaja(1L, new BigDecimal("150000"));

        assertEquals(new BigDecimal("150000"), result.getSaldoCalculado());
        assertEquals(BigDecimal.ZERO, result.getDiferenciaCuadre());
        verify(repository).save(any(AperturaCierre.class));
    }

    @Test
    void cerrarCaja_sesionNoExiste_lanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.cerrarCaja(99L, new BigDecimal("100000")));
    }

    @Test
    void cerrarCaja_cajaYaCerrada_lanzaReglaNegocio() {
        AperturaCierre sesion = new AperturaCierre(1L, cajaCerrada(), 50L, new BigDecimal("100000"), null, APERTURA_DT, null);
        when(repository.findById(1L)).thenReturn(Optional.of(sesion));

        assertThrows(ReglaNegocioException.class, () -> service.cerrarCaja(1L, new BigDecimal("100000")));
    }

    @Test
    void cerrarCaja_sesionYaCerrada_lanzaReglaNegocio() {
        AperturaCierre sesion = new AperturaCierre(1L, cajaAbierta(), 50L, new BigDecimal("100000"), new BigDecimal("100000"), APERTURA_DT, CIERRE_DT);
        when(repository.findById(1L)).thenReturn(Optional.of(sesion));

        assertThrows(ReglaNegocioException.class, () -> service.cerrarCaja(1L, new BigDecimal("100000")));
    }

    @Test
    void obtenerPorId_encontrado_retornaResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(sesionAbierta()));

        AperturaCierreResponse result = service.obtenerPorId(1L);

        assertEquals(1L, result.getIdSesion());
        assertEquals(1L, result.getIdCaja());
    }

    @Test
    void obtenerPorId_noExiste_lanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void listarPorCaja_retornaTodasLasSesiones() {
        when(repository.findByCajaIdCaja(1L)).thenReturn(List.of(sesionAbierta(), sesionCerrada()));

        List<AperturaCierreResponse> result = service.listarPorCaja(1L);

        assertEquals(2, result.size());
    }

    @Test
    void obtenerSesionAbierta_encontrada_retornaResponse() {
        when(repository.findByCajaIdCajaAndFechaHoraCierreIsNull(1L)).thenReturn(Optional.of(sesionAbierta()));

        AperturaCierreResponse result = service.obtenerSesionAbierta(1L);

        assertEquals(1L, result.getIdSesion());
        assertNull(result.getFechaHoraCierre());
    }

    @Test
    void obtenerSesionAbierta_noHaySesion_lanzaRecursoNoEncontrado() {
        when(repository.findByCajaIdCajaAndFechaHoraCierreIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerSesionAbierta(1L));
    }

    @Test
    void generarReporteCierre_sesionCerrada_retornaReporteCompleto() {
        AperturaCierre sesion = sesionCerrada();
        MovimientoCaja ingreso = new MovimientoCaja(1L, sesion, TipoMovimiento.INGRESO, new BigDecimal("80000"), 101L, null);
        MovimientoCaja egreso = new MovimientoCaja(2L, sesion, TipoMovimiento.EGRESO, new BigDecimal("30000"), 102L, null);

        when(repository.findById(1L)).thenReturn(Optional.of(sesion));
        when(movimientoRepository.findBySesionIdSesion(1L)).thenReturn(List.of(ingreso, egreso));

        ReporteCierreResponse result = service.generarReporteCierre(1L);

        assertEquals(1L, result.getIdSesion());
        assertEquals(new BigDecimal("80000"), result.getTotalIngresos());
        assertEquals(new BigDecimal("30000"), result.getTotalEgresos());
        assertEquals(new BigDecimal("150000"), result.getSaldoCalculado());
        assertEquals(BigDecimal.ZERO, result.getDiferenciaCuadre());
    }

    @Test
    void generarReporteCierre_sesionAbierta_lanzaReglaNegocioException() {
        when(repository.findById(1L)).thenReturn(Optional.of(sesionAbierta()));

        assertThrows(ReglaNegocioException.class, () -> service.generarReporteCierre(1L));
    }

    @Test
    void generarReporteCierre_sesionNoExiste_lanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.generarReporteCierre(99L));
    }
}
