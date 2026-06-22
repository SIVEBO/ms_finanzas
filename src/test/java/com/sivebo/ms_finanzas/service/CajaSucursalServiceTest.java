package com.sivebo.ms_finanzas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.exception.RecursoNoEncontradoException;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.model.enums.EstadoCaja;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;

@ExtendWith(MockitoExtension.class)
class CajaSucursalServiceTest {

    @Mock CajaSucursalRepository repository;

    @InjectMocks CajaSucursalService service;

    private static final CajaSucursal CAJA = new CajaSucursal(1L, 10L, EstadoCaja.CERRADA);

    @Test
    void crearRequestValidoGuardaYRetornaResponse() {
        CajaSucursalRequest request = new CajaSucursalRequest(10L, EstadoCaja.CERRADA);
        when(repository.save(any(CajaSucursal.class))).thenReturn(CAJA);

        CajaSucursalResponse result = service.crear(request);

        assertEquals(1L, result.getIdCaja());
        assertEquals(10L, result.getIdSucursal());
        assertEquals("CERRADA", result.getEstadoActual());
        verify(repository).save(any(CajaSucursal.class));
    }

    @Test
    void obtenerPorIdEncontradaRetornaResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(CAJA));

        CajaSucursalResponse result = service.obtenerPorId(1L);

        assertEquals(1L, result.getIdCaja());
        assertEquals(10L, result.getIdSucursal());
    }

    @Test
    void obtenerPorIdNoExisteLanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void obtenerPorSucursalEncontradaRetornaResponse() {
        when(repository.findByIdSucursal(10L)).thenReturn(Optional.of(CAJA));

        CajaSucursalResponse result = service.obtenerPorSucursal(10L);

        assertEquals(1L, result.getIdCaja());
        assertEquals(10L, result.getIdSucursal());
    }

    @Test
    void obtenerPorSucursalNoExisteLanzaRecursoNoEncontrado() {
        when(repository.findByIdSucursal(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorSucursal(99L));
    }

    @Test
    void listarTodasRetornaTodasLasCajas() {
        CajaSucursal caja2 = new CajaSucursal(2L, 20L, EstadoCaja.ABIERTA);
        when(repository.findAll()).thenReturn(List.of(CAJA, caja2));

        List<CajaSucursalResponse> result = service.listarTodas();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getIdCaja());
        assertEquals(2L, result.get(1).getIdCaja());
    }

    @Test
    void actualizarEstadoEncontradaActualizaYRetornaResponse() {
        CajaSucursal cajaActualizada = new CajaSucursal(1L, 10L, EstadoCaja.ABIERTA);
        when(repository.findById(1L)).thenReturn(Optional.of(new CajaSucursal(1L, 10L, EstadoCaja.CERRADA)));
        when(repository.save(any(CajaSucursal.class))).thenReturn(cajaActualizada);

        CajaSucursalResponse result = service.actualizarEstado(1L, EstadoCaja.ABIERTA);

        assertEquals("ABIERTA", result.getEstadoActual());
        verify(repository).save(any(CajaSucursal.class));
    }

    @Test
    void actualizarEstadoNoExisteLanzaRecursoNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizarEstado(99L, EstadoCaja.ABIERTA));
        verify(repository, never()).save(any());
    }
}
