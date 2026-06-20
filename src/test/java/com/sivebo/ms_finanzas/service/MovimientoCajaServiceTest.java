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

import com.sivebo.ms_finanzas.dto.request.MovimientoCajaRequest;
import com.sivebo.ms_finanzas.dto.response.MovimientoCajaResponse;
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;

@ExtendWith(MockitoExtension.class)
class MovimientoCajaServiceTest {

    @Mock MovimientoCajaRepository repository;
    @Mock AperturaCierreRepository aperturaCierreRepository;

    @InjectMocks MovimientoCajaService service;

    private static final CajaSucursal CAJA = new CajaSucursal(1L, 10L, "ABIERTA");

    private static final AperturaCierre SESION = new AperturaCierre(
            1L, CAJA, 50L, new BigDecimal("100000"), null,
            LocalDateTime.of(2026, 6, 1, 8, 0), null);

    private MovimientoCaja buildMovimiento(Long id, String tipo, BigDecimal monto) {
        return new MovimientoCaja(id, SESION, tipo, monto, null);
    }

    @Test
    void registrar_sesionExiste_guardaYRetornaResponse() {
        MovimientoCajaRequest request = new MovimientoCajaRequest(1L, "INGRESO", new BigDecimal("50000"), 101L);
        MovimientoCaja guardado = new MovimientoCaja(1L, SESION, "INGRESO", new BigDecimal("50000"), 101L);

        when(aperturaCierreRepository.findById(1L)).thenReturn(Optional.of(SESION));
        when(repository.save(any(MovimientoCaja.class))).thenReturn(guardado);

        MovimientoCajaResponse result = service.registrar(request);

        assertEquals(1L, result.getIdMovimiento());
        assertEquals(1L, result.getIdSesion());
        assertEquals("INGRESO", result.getTipo());
        assertEquals(new BigDecimal("50000"), result.getMonto());
        verify(repository).save(any(MovimientoCaja.class));
    }

    @Test
    void registrar_sesionNoExiste_lanzaRuntimeException() {
        MovimientoCajaRequest request = new MovimientoCajaRequest(99L, "INGRESO", new BigDecimal("50000"), null);
        when(aperturaCierreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.registrar(request));
        verify(repository, never()).save(any());
    }

    @Test
    void listarPorSesion_retornaTodosLosMovimientos() {
        List<MovimientoCaja> movimientos = List.of(
                buildMovimiento(1L, "INGRESO", new BigDecimal("50000")),
                buildMovimiento(2L, "EGRESO", new BigDecimal("20000")));
        when(repository.findBySesionIdSesion(1L)).thenReturn(movimientos);

        List<MovimientoCajaResponse> result = service.listarPorSesion(1L);

        assertEquals(2, result.size());
        assertEquals("INGRESO", result.get(0).getTipo());
        assertEquals("EGRESO", result.get(1).getTipo());
    }

    @Test
    void listarPorSesionYTipo_filtraPorTipoCorrectamente() {
        List<MovimientoCaja> ingresos = List.of(
                buildMovimiento(1L, "INGRESO", new BigDecimal("50000")),
                buildMovimiento(3L, "INGRESO", new BigDecimal("30000")));
        when(repository.findBySesionIdSesionAndTipo(1L, "INGRESO")).thenReturn(ingresos);

        List<MovimientoCajaResponse> result = service.listarPorSesionYTipo(1L, "INGRESO");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> "INGRESO".equals(r.getTipo())));
    }

    @Test
    void listarPorSesion_sinMovimientos_retornaListaVacia() {
        when(repository.findBySesionIdSesion(1L)).thenReturn(List.of());

        assertTrue(service.listarPorSesion(1L).isEmpty());
    }
}
