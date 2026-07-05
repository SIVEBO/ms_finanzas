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

    private static final String SUCURSAL = "Sucursal Centro";
    private static final String COD_SESION = "SES-ABIERTA1";
    private static final LocalDateTime APERTURADT = LocalDateTime.of(2026, 6, 1, 8, 0);
    private static final LocalDateTime CIERREDT = LocalDateTime.of(2026, 6, 1, 18, 0);

    private CajaSucursal cajaAbierta() {
        return new CajaSucursal(1L, SUCURSAL, EstadoCaja.ABIERTA);
    }

    private CajaSucursal cajaCerrada() {
        return new CajaSucursal(1L, SUCURSAL, EstadoCaja.CERRADA);
    }

    private AperturaCierre sesionAbierta() {
        return new AperturaCierre(1L, COD_SESION, SUCURSAL, "operador1", new BigDecimal("100000"), null, APERTURADT, null);
    }

    private AperturaCierre sesionCerrada() {
        return new AperturaCierre(1L, COD_SESION, SUCURSAL, "operador1", new BigDecimal("100000"), new BigDecimal("150000"),
                APERTURADT, CIERREDT);
    }

    @Test
    void abrirCajaCajaExisteCreaYRetornaSesion() {
        AperturaCierreRequest request = new AperturaCierreRequest(SUCURSAL, "operador1", new BigDecimal("100000"));
        AperturaCierre sesionGuardada = sesionAbierta();

        when(cajaRepository.findByNombreSucursal(SUCURSAL)).thenReturn(Optional.of(cajaCerrada()));
        when(cajaRepository.save(any(CajaSucursal.class))).thenReturn(cajaAbierta());
        when(repository.save(any(AperturaCierre.class))).thenReturn(sesionGuardada);

        AperturaCierreResponse result = service.abrirCaja(request);

        assertEquals(1L, result.getIdSesion());
        assertEquals(new BigDecimal("100000"), result.getMontoApertura());
        verify(cajaRepository).save(any(CajaSucursal.class));
        verify(repository).save(any(AperturaCierre.class));
    }

    @Test
    void abrirCajaCajaNoExisteLanzaRecursoNoEncontrado() {
        AperturaCierreRequest request = new AperturaCierreRequest("Sucursal Inexistente", "operador1", new BigDecimal("100000"));
        when(cajaRepository.findByNombreSucursal("Sucursal Inexistente")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.abrirCaja(request));
        verify(repository, never()).save(any());
    }

    @Test
    void abrirCajaCajaYaAbiertaLanzaReglaNegocio() {
        AperturaCierreRequest request = new AperturaCierreRequest(SUCURSAL, "operador1", new BigDecimal("100000"));
        when(cajaRepository.findByNombreSucursal(SUCURSAL)).thenReturn(Optional.of(cajaAbierta()));

        assertThrows(ReglaNegocioException.class, () -> service.abrirCaja(request));
        verify(repository, never()).save(any());
    }

    @Test
    void cerrarCajaCalculaCuadreCorrectamente() {
        AperturaCierre sesion = sesionAbierta();
        MovimientoCaja ingreso = new MovimientoCaja(1L, COD_SESION, TipoMovimiento.INGRESO, new BigDecimal("80000"), "101", null);
        MovimientoCaja egreso = new MovimientoCaja(2L, COD_SESION, TipoMovimiento.EGRESO, new BigDecimal("30000"), "102", null);
        AperturaCierre sesionCerrada = sesionCerrada();

        when(repository.findById(1L)).thenReturn(Optional.of(sesion));
        when(cajaRepository.findByNombreSucursal(SUCURSAL)).thenReturn(Optional.of(cajaAbierta()));
        when(movimientoRepository.findByCodSesion(COD_SESION)).thenReturn(List.of(ingreso, egreso));
        when(cajaRepository.save(any(CajaSucursal.class))).thenReturn(cajaCerrada());
        when(repository.save(any(AperturaCierre.class))).thenReturn(sesionCerrada);

        // saldoCalculado = 100000 + 80000 - 30000 = 150000; diferencia = 150000 - 150000 = 0
        AperturaCierreResponse result = service.cerrarCaja(1L, new BigDecimal("150000"));

        assertEquals(new BigDecimal("150000"), result.getSaldoCalculado());
        assertEquals(BigDecimal.ZERO, result.getDiferenciaCuadre());
        verify(repository).save(any(AperturaCierre.class));
    }

    @Test
    void cerrarCajaSesionNoExisteLanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.cerrarCaja(99L, new BigDecimal("100000")));
    }

    @Test
    void cerrarCajaCajaYaCerradaLanzaReglaNegocio() {
        AperturaCierre sesion = new AperturaCierre(1L, COD_SESION, SUCURSAL, "operador1", new BigDecimal("100000"), null, APERTURADT, null);
        when(repository.findById(1L)).thenReturn(Optional.of(sesion));
        when(cajaRepository.findByNombreSucursal(SUCURSAL)).thenReturn(Optional.of(cajaCerrada()));

        assertThrows(ReglaNegocioException.class, () -> service.cerrarCaja(1L, new BigDecimal("100000")));
    }

    @Test
    void cerrarCajaSesionYaCerradaLanzaReglaNegocio() {
        AperturaCierre sesion = new AperturaCierre(1L, COD_SESION, SUCURSAL, "operador1", new BigDecimal("100000"), new BigDecimal("100000"), APERTURADT, CIERREDT);
        when(repository.findById(1L)).thenReturn(Optional.of(sesion));
        when(cajaRepository.findByNombreSucursal(SUCURSAL)).thenReturn(Optional.of(cajaAbierta()));

        assertThrows(ReglaNegocioException.class, () -> service.cerrarCaja(1L, new BigDecimal("100000")));
    }

    @Test
    void obtenerPorIdEncontradoRetornaResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(sesionAbierta()));

        AperturaCierreResponse result = service.obtenerPorId(1L);

        assertEquals(1L, result.getIdSesion());
        assertEquals(SUCURSAL, result.getNombreSucursal());
    }

    @Test
    void obtenerPorIdNoExisteLanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void listarPorCajaRetornaTodasLasSesiones() {
        when(repository.findByNombreSucursal(SUCURSAL)).thenReturn(List.of(sesionAbierta(), sesionCerrada()));

        List<AperturaCierreResponse> result = service.listarPorCaja(SUCURSAL);

        assertEquals(2, result.size());
    }

    @Test
    void obtenerSesionAbiertaEncontradaRetornaResponse() {
        when(repository.findByNombreSucursalAndFechaHoraCiIsNull(SUCURSAL)).thenReturn(Optional.of(sesionAbierta()));

        AperturaCierreResponse result = service.obtenerSesionAbierta(SUCURSAL);

        assertEquals(1L, result.getIdSesion());
        assertNull(result.getFechaHoraCi());
    }

    @Test
    void obtenerSesionAbiertaNoHaySesionLanzaRecursoNoEncontrado() {
        when(repository.findByNombreSucursalAndFechaHoraCiIsNull(SUCURSAL)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerSesionAbierta(SUCURSAL));
    }

    @Test
    void generarReporteCierreSesionCerradaRetornaReporteCompleto() {
        AperturaCierre sesion = sesionCerrada();
        MovimientoCaja ingreso = new MovimientoCaja(1L, COD_SESION, TipoMovimiento.INGRESO, new BigDecimal("80000"), "101", null);
        MovimientoCaja egreso = new MovimientoCaja(2L, COD_SESION, TipoMovimiento.EGRESO, new BigDecimal("30000"), "102", null);

        when(repository.findById(1L)).thenReturn(Optional.of(sesion));
        when(movimientoRepository.findByCodSesion(COD_SESION)).thenReturn(List.of(ingreso, egreso));

        ReporteCierreResponse result = service.generarReporteCierre(1L);

        assertEquals(1L, result.getIdSesion());
        assertEquals(new BigDecimal("80000"), result.getTotalIngresos());
        assertEquals(new BigDecimal("30000"), result.getTotalEgresos());
        assertEquals(new BigDecimal("150000"), result.getSaldoCalculado());
        assertEquals(BigDecimal.ZERO, result.getDiferenciaCuadre());
    }

    @Test
    void generarReporteCierreSesionAbiertaLanzaReglaNegocioException() {
        when(repository.findById(1L)).thenReturn(Optional.of(sesionAbierta()));

        assertThrows(ReglaNegocioException.class, () -> service.generarReporteCierre(1L));
    }

    @Test
    void generarReporteCierreSesionNoExisteLanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.generarReporteCierre(99L));
    }
}
