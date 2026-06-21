package com.sivebo.ms_finanzas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sivebo.ms_finanzas.dto.request.AperturaCierreRequest;
import com.sivebo.ms_finanzas.dto.response.AperturaCierreResponse;
import com.sivebo.ms_finanzas.dto.response.ReporteCierreResponse;
<<<<<<< HEAD
import com.sivebo.ms_finanzas.exception.RecursoNoEncontradoException;
import com.sivebo.ms_finanzas.exception.ReglaNegocioException;
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
import com.sivebo.ms_finanzas.model.enums.EstadoCaja;
import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;
=======
import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
>>>>>>> refactor
import com.sivebo.ms_finanzas.repository.AperturaCierreRepository;
import com.sivebo.ms_finanzas.repository.CajaSucursalRepository;
import com.sivebo.ms_finanzas.repository.MovimientoCajaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AperturaCierreService {  

    private final AperturaCierreRepository repository;
    private final CajaSucursalRepository cajaRepository;
    private final MovimientoCajaRepository movimientoRepository;

    public AperturaCierreResponse abrirCaja(AperturaCierreRequest request) {
        log.info("Abriendo caja id: {}", request.getIdCaja());
        CajaSucursal caja = cajaRepository.findById(request.getIdCaja())
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja no encontrada"));

        if (caja.getEstadoActual() == EstadoCaja.ABIERTA) {
            throw new ReglaNegocioException("La caja ya se encuentra abierta");
        }

        caja.setEstadoActual(EstadoCaja.ABIERTA);
        cajaRepository.save(caja);

        AperturaCierre apertura = new AperturaCierre();
        apertura.setCaja(caja);
        apertura.setIdUsuario(request.getIdUsuario());
        apertura.setMontoApertura(request.getMontoApertura());
        apertura.setFechaHoraApertura(LocalDateTime.now());
        return toResponse(repository.save(apertura));
    }

    public AperturaCierreResponse cerrarCaja(Long idSesion, BigDecimal montoCierre) {
        log.info("Cerrando sesión id: {}", idSesion);
        AperturaCierre sesion = repository.findById(idSesion)
<<<<<<< HEAD
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada"));

        if (sesion.getCaja().getEstadoActual() == EstadoCaja.CERRADA) {
            throw new ReglaNegocioException("La caja ya se encuentra cerrada");
        }
        if (sesion.getFechaHoraCierre() != null) {
            throw new ReglaNegocioException("Esta sesión ya fue cerrada anteriormente");
        }
=======
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // RF-39: cuadre — compute expected balance from movimientos
        List<MovimientoCaja> movimientos = movimientoRepository.findBySesionIdSesion(idSesion);
        BigDecimal totalIngresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoCalculado = sesion.getMontoApertura().add(totalIngresos).subtract(totalEgresos);
        BigDecimal diferencia = montoCierre.subtract(saldoCalculado);
        log.info("Cuadre sesión {}: saldoCalculado={}, declarado={}, diferencia={}",
                idSesion, saldoCalculado, montoCierre, diferencia);
>>>>>>> refactor

        sesion.setMontoCierre(montoCierre);
        sesion.setFechaHoraCierre(LocalDateTime.now());
        sesion.getCaja().setEstadoActual(EstadoCaja.CERRADA);
        cajaRepository.save(sesion.getCaja());
        AperturaCierreResponse r = toResponse(repository.save(sesion));
        r.setSaldoCalculado(saldoCalculado);
        r.setDiferenciaCuadre(diferencia);
        return r;
    }

    public AperturaCierreResponse obtenerPorId(Long id) {
        log.info("Buscando sesión id: {}", id);
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada")));
    }

    public List<AperturaCierreResponse> listarPorCaja(Long idCaja) {
        log.info("Listando sesiones de caja id: {}", idCaja);
        return repository.findByCajaIdCaja(idCaja).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AperturaCierreResponse obtenerSesionAbierta(Long idCaja) {
        log.info("Buscando sesión abierta de caja id: {}", idCaja);
        return toResponse(repository.findByCajaIdCajaAndFechaHoraCierreIsNull(idCaja)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay sesión abierta para esta caja")));
    }

    public ReporteCierreResponse generarReporteCierre(Long idSesion) {
        log.info("Generando reporte de cierre para sesión id: {}", idSesion);
        AperturaCierre sesion = repository.findById(idSesion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada"));

        if (sesion.getMontoCierre() == null) {
            throw new ReglaNegocioException("La sesión aún no ha sido cerrada");
        }

        List<MovimientoCaja> movimientos = movimientoRepository.findBySesionIdSesion(idSesion);

        BigDecimal totalIngresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEgresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.EGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoEsperado = sesion.getMontoApertura().add(totalIngresos).subtract(totalEgresos);
        BigDecimal diferencia = sesion.getMontoCierre().subtract(montoEsperado);

        ReporteCierreResponse reporte = new ReporteCierreResponse();
        reporte.setIdSesion(idSesion);
        reporte.setIdCaja(sesion.getCaja().getIdCaja());
        reporte.setMontoApertura(sesion.getMontoApertura());
        reporte.setTotalIngresos(totalIngresos);
        reporte.setTotalEgresos(totalEgresos);
        reporte.setMontoEsperado(montoEsperado);
        reporte.setMontoCierreDeclarado(sesion.getMontoCierre());
        reporte.setDiferenciaCuadre(diferencia);
        return reporte;
    }

    // RF-40: reporte de cierre con cuadre
    public ReporteCierreResponse generarReporteCierre(Long idSesion) {
        log.info("Generando reporte de cierre para sesión id: {}", idSesion);
        AperturaCierre sesion = repository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        if (sesion.getFechaHoraCierre() == null) {
            throw new RuntimeException("La sesión " + idSesion + " aún está abierta");
        }
        List<MovimientoCaja> movimientos = movimientoRepository.findBySesionIdSesion(idSesion);
        BigDecimal totalIngresos = movimientos.stream()
                .filter(m -> "INGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresos = movimientos.stream()
                .filter(m -> "EGRESO".equals(m.getTipo()))
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoCalculado = sesion.getMontoApertura().add(totalIngresos).subtract(totalEgresos);
        BigDecimal diferencia = sesion.getMontoCierre().subtract(saldoCalculado);

        ReporteCierreResponse r = new ReporteCierreResponse();
        r.setIdSesion(sesion.getIdSesion());
        r.setIdCaja(sesion.getCaja().getIdCaja());
        r.setIdUsuario(sesion.getIdUsuario());
        r.setFechaHoraApertura(sesion.getFechaHoraApertura());
        r.setFechaHoraCierre(sesion.getFechaHoraCierre());
        r.setMontoApertura(sesion.getMontoApertura());
        r.setMontoCierre(sesion.getMontoCierre());
        r.setTotalIngresos(totalIngresos);
        r.setTotalEgresos(totalEgresos);
        r.setSaldoCalculado(saldoCalculado);
        r.setDiferenciaCuadre(diferencia);
        return r;
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
