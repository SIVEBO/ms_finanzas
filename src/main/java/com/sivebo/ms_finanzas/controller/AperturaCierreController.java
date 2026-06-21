package com.sivebo.ms_finanzas.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_finanzas.dto.request.AperturaCierreRequest;
import com.sivebo.ms_finanzas.dto.response.AperturaCierreResponse;
import com.sivebo.ms_finanzas.dto.response.ReporteCierreResponse;
import com.sivebo.ms_finanzas.service.AperturaCierreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Apertura y Cierre de Caja", description = "RF-36/39/40: sesiones de apertura y cierre de caja por sucursal")
@RestController
@RequestMapping("api/v1/aperturas")
@RequiredArgsConstructor
public class AperturaCierreController {

    private final AperturaCierreService service;

    @Operation(summary = "Abrir caja", description = "RF-36: inicia sesión de caja con monto inicial")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Caja abierta exitosamente"),
        @ApiResponse(responseCode = "400", description = "La caja ya tiene una sesión abierta")
    })
    @PostMapping("/abrir")
    public ResponseEntity<AperturaCierreResponse> abrirCaja(@Valid @RequestBody AperturaCierreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.abrirCaja(request));
    }

    @Operation(summary = "Cerrar caja", description = "RF-39: cierra la sesión declarando el monto final contado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caja cerrada"),
        @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    })
    @PatchMapping("/{idSesion}/cerrar")
    public ResponseEntity<AperturaCierreResponse> cerrarCaja(@PathVariable Long idSesion, @RequestParam BigDecimal montoCierre) {
        return ResponseEntity.ok(service.cerrarCaja(idSesion, montoCierre));
    }

    @Operation(summary = "Obtener sesión por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sesión encontrada"),
        @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AperturaCierreResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Listar sesiones de una caja")
    @ApiResponse(responseCode = "200", description = "Historial de sesiones")
    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<List<AperturaCierreResponse>> listarPorCaja(@PathVariable Long idCaja) {
        return ResponseEntity.ok(service.listarPorCaja(idCaja));
    }

    @Operation(summary = "Obtener sesión actualmente abierta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sesión abierta encontrada"),
        @ApiResponse(responseCode = "404", description = "No hay sesión abierta para esta caja")
    })
    @GetMapping("/caja/{idCaja}/abierta")
    public ResponseEntity<AperturaCierreResponse> obtenerSesionAbierta(@PathVariable Long idCaja) {
        return ResponseEntity.ok(service.obtenerSesionAbierta(idCaja));
    }

<<<<<<< HEAD
=======
    @Operation(summary = "Generar reporte de cierre", description = "RF-40: reporte con ingresos, egresos y diferencia de cuadre")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado"),
        @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    })
>>>>>>> refactor
    @GetMapping("/{idSesion}/reporte-cierre")
    public ResponseEntity<ReporteCierreResponse> reporteCierre(@PathVariable Long idSesion) {
        return ResponseEntity.ok(service.generarReporteCierre(idSesion));
    }
}