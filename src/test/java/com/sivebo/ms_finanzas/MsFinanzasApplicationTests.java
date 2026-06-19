package com.sivebo.ms_finanzas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sivebo.ms_finanzas.dto.request.AperturaCierreRequest;
import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.request.MovimientoCajaRequest;
import com.sivebo.ms_finanzas.dto.response.AperturaCierreResponse;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.dto.response.MovimientoCajaResponse;
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
import com.sivebo.ms_finanzas.service.AperturaCierreService;
import com.sivebo.ms_finanzas.service.CajaSucursalService;
import com.sivebo.ms_finanzas.service.MovimientoCajaService;

@ExtendWith(MockitoExtension.class)
class MsFinanzasApplicationTests {

    @Nested
    class AperturaCierreTests {

        @Mock
        private AperturaCierreRepository repository;
        @Mock
        private CajaSucursalRepository cajaRepository;
        @Mock
        private MovimientoCajaRepository movimientoRepository;
        @InjectMocks
        private AperturaCierreService service;

        private CajaSucursal caja;
        private AperturaCierre sesion;

        @BeforeEach
        void setUp() {
            caja = new CajaSucursal();
            caja.setIdCaja(1L);
            caja.setIdSucursal(10L);
            caja.setEstadoActual(EstadoCaja.CERRADA);

            sesion = new AperturaCierre();
            sesion.setIdSesion(1L);
            sesion.setCaja(caja);
            sesion.setIdUsuario(5L);
            sesion.setMontoApertura(new BigDecimal("50000"));
            sesion.setFechaHoraApertura(LocalDateTime.now());
        }

        @Test
        void abrirCaja_cajaCerrada_abreCorrectamente() {
            AperturaCierreRequest request = new AperturaCierreRequest();
            request.setIdCaja(1L);
            request.setIdUsuario(5L);
            request.setMontoApertura(new BigDecimal("50000"));

            when(cajaRepository.findById(1L)).thenReturn(Optional.of(caja));
            when(cajaRepository.save(any())).thenReturn(caja);
            when(repository.save(any(AperturaCierre.class))).thenReturn(sesion);

            AperturaCierreResponse response = service.abrirCaja(request);

            assertNotNull(response);
            assertEquals(EstadoCaja.ABIERTA, caja.getEstadoActual());
            verify(cajaRepository).save(caja);
        }

        @Test
        void abrirCaja_cajaYaAbierta_lanzaExcepcion() {
            caja.setEstadoActual(EstadoCaja.ABIERTA);
            AperturaCierreRequest request = new AperturaCierreRequest();
            request.setIdCaja(1L);
            request.setIdUsuario(5L);
            request.setMontoApertura(new BigDecimal("50000"));

            when(cajaRepository.findById(1L)).thenReturn(Optional.of(caja));

            assertThrows(ReglaNegocioException.class, () -> service.abrirCaja(request));
        }

        @Test
        void abrirCaja_cajaNoExiste_lanzaExcepcion() {
            AperturaCierreRequest request = new AperturaCierreRequest();
            request.setIdCaja(99L);
            request.setIdUsuario(5L);
            request.setMontoApertura(new BigDecimal("50000"));

            when(cajaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class, () -> service.abrirCaja(request));
        }

        @Test
        void cerrarCaja_sesionAbierta_cierraCorrectamente() {
            caja.setEstadoActual(EstadoCaja.ABIERTA);

            when(repository.findById(1L)).thenReturn(Optional.of(sesion));
            when(cajaRepository.save(any())).thenReturn(caja);
            when(repository.save(any(AperturaCierre.class))).thenReturn(sesion);

            AperturaCierreResponse response = service.cerrarCaja(1L, new BigDecimal("48000"));

            assertNotNull(response);
            assertEquals(EstadoCaja.CERRADA, caja.getEstadoActual());
        }

        @Test
        void cerrarCaja_cajaYaCerrada_lanzaExcepcion() {
            when(repository.findById(1L)).thenReturn(Optional.of(sesion));

            assertThrows(ReglaNegocioException.class,
                    () -> service.cerrarCaja(1L, new BigDecimal("48000")));
        }

        @Test
        void cerrarCaja_sesionYaCerrada_lanzaExcepcion() {
            caja.setEstadoActual(EstadoCaja.ABIERTA);
            sesion.setFechaHoraCierre(LocalDateTime.now());

            when(repository.findById(1L)).thenReturn(Optional.of(sesion));

            assertThrows(ReglaNegocioException.class,
                    () -> service.cerrarCaja(1L, new BigDecimal("48000")));
        }

        @Test
        void generarReporteCierre_calculaCorrectamente() {
            sesion.setMontoCierre(new BigDecimal("55000"));
            sesion.setFechaHoraCierre(LocalDateTime.now());

            MovimientoCaja ingreso = new MovimientoCaja();
            ingreso.setTipo(TipoMovimiento.INGRESO);
            ingreso.setMonto(new BigDecimal("10000"));
            ingreso.setSesion(sesion);

            MovimientoCaja egreso = new MovimientoCaja();
            egreso.setTipo(TipoMovimiento.EGRESO);
            egreso.setMonto(new BigDecimal("3000"));
            egreso.setSesion(sesion);

            when(repository.findById(1L)).thenReturn(Optional.of(sesion));
            when(movimientoRepository.findBySesionIdSesion(1L)).thenReturn(List.of(ingreso, egreso));

            ReporteCierreResponse reporte = service.generarReporteCierre(1L);

            assertEquals(new BigDecimal("50000"), reporte.getMontoApertura());
            assertEquals(new BigDecimal("10000"), reporte.getTotalIngresos());
            assertEquals(new BigDecimal("3000"), reporte.getTotalEgresos());
            assertEquals(new BigDecimal("57000"), reporte.getMontoEsperado());
            assertEquals(new BigDecimal("55000"), reporte.getMontoCierreDeclarado());
            assertEquals(new BigDecimal("-2000"), reporte.getDiferenciaCuadre());
        }

        @Test
        void generarReporteCierre_sesionNoCerrada_lanzaExcepcion() {
            sesion.setMontoCierre(null);
            when(repository.findById(1L)).thenReturn(Optional.of(sesion));

            assertThrows(ReglaNegocioException.class, () -> service.generarReporteCierre(1L));
        }
    }

    @Nested
    class CajaSucursalTests {

        @Mock
        private CajaSucursalRepository repository;
        @InjectMocks
        private CajaSucursalService service;

        @Test
        void crear_siempreIniciaCerrada() {
            CajaSucursalRequest request = new CajaSucursalRequest();
            request.setIdSucursal(10L);

            CajaSucursal caja = new CajaSucursal();
            caja.setIdCaja(1L);
            caja.setIdSucursal(10L);
            caja.setEstadoActual(EstadoCaja.CERRADA);

            when(repository.save(any(CajaSucursal.class))).thenReturn(caja);

            CajaSucursalResponse response = service.crear(request);

            assertEquals("CERRADA", response.getEstadoActual());
        }

        @Test
        void obtenerPorId_noExiste_lanzaExcepcion() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
        }
    }

    @Nested
    class MovimientoCajaTests {

        @Mock
        private MovimientoCajaRepository repository;
        @Mock
        private AperturaCierreRepository aperturaCierreRepository;
        @InjectMocks
        private MovimientoCajaService service;

        private AperturaCierre sesion;

        @BeforeEach
        void setUp() {
            CajaSucursal caja = new CajaSucursal();
            caja.setIdCaja(1L);
            caja.setEstadoActual(EstadoCaja.ABIERTA);

            sesion = new AperturaCierre();
            sesion.setIdSesion(1L);
            sesion.setCaja(caja);
        }

        @Test
        void registrar_egresoManualSinConcepto_lanzaExcepcion() {
            MovimientoCajaRequest request = new MovimientoCajaRequest();
            request.setIdSesion(1L);
            request.setTipo(TipoMovimiento.EGRESO);
            request.setMonto(new BigDecimal("5000"));
            request.setConcepto(null);
            request.setIdReferenciaVta(null);

            when(aperturaCierreRepository.findById(1L)).thenReturn(Optional.of(sesion));

            assertThrows(ReglaNegocioException.class, () -> service.registrar(request));
        }

        @Test
        void registrar_egresoManualConConcepto_registraCorrectamente() {
            MovimientoCajaRequest request = new MovimientoCajaRequest();
            request.setIdSesion(1L);
            request.setTipo(TipoMovimiento.EGRESO);
            request.setMonto(new BigDecimal("5000"));
            request.setConcepto("Compra de insumos");
            request.setIdReferenciaVta(null);

            MovimientoCaja mov = new MovimientoCaja();
            mov.setIdMovimiento(1L);
            mov.setSesion(sesion);
            mov.setTipo(TipoMovimiento.EGRESO);
            mov.setMonto(new BigDecimal("5000"));
            mov.setConcepto("Compra de insumos");

            when(aperturaCierreRepository.findById(1L)).thenReturn(Optional.of(sesion));
            when(repository.save(any(MovimientoCaja.class))).thenReturn(mov);

            MovimientoCajaResponse response = service.registrar(request);

            assertEquals("EGRESO", response.getTipo());
            assertEquals("Compra de insumos", response.getConcepto());
        }

        @Test
        void registrar_ingresoSinConcepto_noLanzaExcepcion() {
            MovimientoCajaRequest request = new MovimientoCajaRequest();
            request.setIdSesion(1L);
            request.setTipo(TipoMovimiento.INGRESO);
            request.setMonto(new BigDecimal("15000"));
            request.setIdReferenciaVta(50L);

            MovimientoCaja mov = new MovimientoCaja();
            mov.setIdMovimiento(1L);
            mov.setSesion(sesion);
            mov.setTipo(TipoMovimiento.INGRESO);
            mov.setMonto(new BigDecimal("15000"));
            mov.setIdReferenciaVta(50L);

            when(aperturaCierreRepository.findById(1L)).thenReturn(Optional.of(sesion));
            when(repository.save(any(MovimientoCaja.class))).thenReturn(mov);

            MovimientoCajaResponse response = service.registrar(request);

            assertEquals("INGRESO", response.getTipo());
        }
    }
}
