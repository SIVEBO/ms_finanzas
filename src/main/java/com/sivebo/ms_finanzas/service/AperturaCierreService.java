package com.sivebo.ms_finanzas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
        log.info("Abriendo caja de sucursal: {}", request.getNombreSucursal());
        CajaSucursal caja = cajaRepository.findByNombreSucursal(request.getNombreSucursal())
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja no encontrada"));
        if (caja.getEstadoActual() == EstadoCaja.ABIERTA) {
            throw new ReglaNegocioException("La caja ya se encuentra abierta");
        }
        caja.setEstadoActual(EstadoCaja.ABIERTA);
        cajaRepository.save(caja);

        AperturaCierre apertura = new AperturaCierre();
        apertura.setCodSesion(generarCodSesion());
        apertura.setNombreSucursal(request.getNombreSucursal());
        apertura.setUsername(request.getUsername());
        apertura.setMontoApertura(request.getMontoApertura());
        apertura.setFechaHoraAp(LocalDateTime.now());
        return toResponse(repository.save(apertura));
    }

    public AperturaCierreResponse cerrarCaja(Long idSesion, BigDecimal montoCierre) {
        log.info("Cerrando sesión id: {}", idSesion);
        AperturaCierre sesion = repository.findById(idSesion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada"));
        CajaSucursal caja = cajaRepository.findByNombreSucursal(sesion.getNombreSucursal())
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja no encontrada"));
        if (caja.getEstadoActual() == EstadoCaja.CERRADA) {
            throw new ReglaNegocioException("La caja ya se encuentra cerrada");
        }
        if (sesion.getFechaHoraCi() != null) {
            throw new ReglaNegocioException("Esta sesión ya fue cerrada anteriormente");
        }

        List<MovimientoCaja> movimientos = movimientoRepository.findByCodSesion(sesion.getCodSesion());
        BigDecimal totalIngresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.EGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoCalculado = sesion.getMontoApertura().add(totalIngresos).subtract(totalEgresos);
        BigDecimal diferencia = montoCierre.subtract(saldoCalculado);
        log.info("Cuadre sesión {}: saldoCalculado={}, declarado={}, diferencia={}",
                idSesion, saldoCalculado, montoCierre, diferencia);

        sesion.setMontoCierre(montoCierre);
        sesion.setFechaHoraCi(LocalDateTime.now());
        caja.setEstadoActual(EstadoCaja.CERRADA);
        cajaRepository.save(caja);
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


    public List<AperturaCierreResponse> listarPorCaja(String nombreSucursal) {
        log.info("Listando sesiones de la sucursal: {}", nombreSucursal);
        return repository.findByNombreSucursal(nombreSucursal).stream().map(this::toResponse).collect(Collectors.toList());
    }


    public AperturaCierreResponse obtenerSesionAbierta(String nombreSucursal) {
        log.info("Buscando sesión abierta de la sucursal: {}", nombreSucursal);
        return toResponse(repository.findByNombreSucursalAndFechaHoraCiIsNull(nombreSucursal)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay sesión abierta para esta caja")));
    }

    public ReporteCierreResponse generarReporteCierre(Long idSesion) {
        log.info("Generando reporte de cierre para sesión id: {}", idSesion);
        AperturaCierre sesion = repository.findById(idSesion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada"));
        if (sesion.getFechaHoraCi() == null) {
            throw new ReglaNegocioException("La sesión " + idSesion + " aún está abierta");
        }
        List<MovimientoCaja> movimientos = movimientoRepository.findByCodSesion(sesion.getCodSesion());
        BigDecimal totalIngresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.EGRESO)
                .map(MovimientoCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoCalculado = sesion.getMontoApertura().add(totalIngresos).subtract(totalEgresos);
        BigDecimal diferencia = sesion.getMontoCierre().subtract(saldoCalculado);

        ReporteCierreResponse r = new ReporteCierreResponse();
        r.setIdSesion(sesion.getIdSesion());
        r.setCodSesion(sesion.getCodSesion());
        r.setNombreSucursal(sesion.getNombreSucursal());
        r.setUsername(sesion.getUsername());
        r.setFechaHoraAp(sesion.getFechaHoraAp());
        r.setFechaHoraCi(sesion.getFechaHoraCi());
        r.setMontoApertura(sesion.getMontoApertura());
        r.setMontoCierre(sesion.getMontoCierre());
        r.setTotalIngresos(totalIngresos);
        r.setTotalEgresos(totalEgresos);
        r.setSaldoCalculado(saldoCalculado);
        r.setDiferenciaCuadre(diferencia);
        return r;
    }

    private String generarCodSesion() {
        return "SES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AperturaCierreResponse toResponse(AperturaCierre a) {
        AperturaCierreResponse r = new AperturaCierreResponse();
        r.setIdSesion(a.getIdSesion());
        r.setCodSesion(a.getCodSesion());
        r.setNombreSucursal(a.getNombreSucursal());
        r.setUsername(a.getUsername());
        r.setMontoApertura(a.getMontoApertura());
        r.setMontoCierre(a.getMontoCierre());
        r.setFechaHoraAp(a.getFechaHoraAp());
        r.setFechaHoraCi(a.getFechaHoraCi());
        return r;
    }

}
