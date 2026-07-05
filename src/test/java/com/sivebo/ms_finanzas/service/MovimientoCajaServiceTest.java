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

@ExtendWith(MockitoExtension.class)
class MovimientoCajaServiceTest {

    @Mock MovimientoCajaRepository repository;
    @Mock AperturaCierreRepository aperturaCierreRepository;
    @Mock FinanzasClient finanzasClient;

    @InjectMocks MovimientoCajaService service;

    private static final String COD_SESION = "SES-ABIERTA1";

    private static final AperturaCierre SESION = new AperturaCierre(
            1L, COD_SESION, "Sucursal Centro", "operador1", new BigDecimal("100000"), null,
            LocalDateTime.of(2026, 6, 1, 8, 0), null);

    private MovimientoCaja buildMovimiento(Long id, TipoMovimiento tipo, BigDecimal monto) {
        return new MovimientoCaja(id, COD_SESION, tipo, monto, null, null);
    }

    @Test
    void registrarSesionExisteGuardaYRetornaResponse() {
        MovimientoCajaRequest request = new MovimientoCajaRequest(COD_SESION, TipoMovimiento.INGRESO, new BigDecimal("50000"), "101", null);
        MovimientoCaja guardado = new MovimientoCaja(1L, COD_SESION, TipoMovimiento.INGRESO, new BigDecimal("50000"), "101", null);

        when(aperturaCierreRepository.findByCodSesion(COD_SESION)).thenReturn(Optional.of(SESION));
        when(finanzasClient.verificarVenta("101")).thenReturn(true);
        when(repository.save(any(MovimientoCaja.class))).thenReturn(guardado);

        MovimientoCajaResponse result = service.registrar(request);

        assertEquals(1L, result.getIdMov());
        assertEquals(COD_SESION, result.getCodSesion());
        assertEquals("INGRESO", result.getTipo());
        assertEquals(new BigDecimal("50000"), result.getMonto());
        verify(repository).save(any(MovimientoCaja.class));
    }

    @Test
    void registrarVentaNoExisteLanzaReglaNegocio() {
        MovimientoCajaRequest request = new MovimientoCajaRequest(COD_SESION, TipoMovimiento.INGRESO, new BigDecimal("50000"), "999", null);

        when(aperturaCierreRepository.findByCodSesion(COD_SESION)).thenReturn(Optional.of(SESION));
        when(finanzasClient.verificarVenta("999")).thenReturn(false);

        assertThrows(ReglaNegocioException.class, () -> service.registrar(request));
        verify(repository, never()).save(any());
    }

    @Test
    void registrarEgresoConConceptoPersisteConcepto() {
        MovimientoCajaRequest request = new MovimientoCajaRequest(COD_SESION, TipoMovimiento.EGRESO, new BigDecimal("15000"), null, "Compra de insumos");
        MovimientoCaja guardado = new MovimientoCaja(2L, COD_SESION, TipoMovimiento.EGRESO, new BigDecimal("15000"), null, "Compra de insumos");

        when(aperturaCierreRepository.findByCodSesion(COD_SESION)).thenReturn(Optional.of(SESION));
        when(repository.save(any(MovimientoCaja.class))).thenReturn(guardado);

        MovimientoCajaResponse result = service.registrar(request);

        assertEquals("EGRESO", result.getTipo());
        assertEquals("Compra de insumos", result.getConcepto());
        verify(finanzasClient, never()).verificarVenta(any());
    }

    @Test
    void registrarSesionNoExisteLanzaRecursoNoEncontrado() {
        MovimientoCajaRequest request = new MovimientoCajaRequest("SES-NOEXISTE", TipoMovimiento.INGRESO, new BigDecimal("50000"), null, null);
        when(aperturaCierreRepository.findByCodSesion("SES-NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(request));
        verify(repository, never()).save(any());
    }

    @Test
    void listarPorSesionRetornaTodosLosMovimientos() {
        List<MovimientoCaja> movimientos = List.of(
                buildMovimiento(1L, TipoMovimiento.INGRESO, new BigDecimal("50000")),
                buildMovimiento(2L, TipoMovimiento.EGRESO, new BigDecimal("20000")));
        when(repository.findByCodSesion(COD_SESION)).thenReturn(movimientos);

        List<MovimientoCajaResponse> result = service.listarPorSesion(COD_SESION);

        assertEquals(2, result.size());
        assertEquals("INGRESO", result.get(0).getTipo());
        assertEquals("EGRESO", result.get(1).getTipo());
    }

    @Test
    void listarPorSesionYTipoFiltraPorTipoCorrectamente() {
        List<MovimientoCaja> ingresos = List.of(
                buildMovimiento(1L, TipoMovimiento.INGRESO, new BigDecimal("50000")),
                buildMovimiento(3L, TipoMovimiento.INGRESO, new BigDecimal("30000")));
        when(repository.findByCodSesionAndTipo(COD_SESION, TipoMovimiento.INGRESO)).thenReturn(ingresos);

        List<MovimientoCajaResponse> result = service.listarPorSesionYTipo(COD_SESION, TipoMovimiento.INGRESO);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> "INGRESO".equals(r.getTipo())));
    }

    @Test
    void listarPorSesionSinMovimientosRetornaListaVacia() {
        when(repository.findByCodSesion(COD_SESION)).thenReturn(List.of());

        assertTrue(service.listarPorSesion(COD_SESION).isEmpty());
    }
}
