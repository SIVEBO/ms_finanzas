package com.sivebo.ms_finanzas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_finanzas.dto.request.MovimientoCajaRequest;
import com.sivebo.ms_finanzas.dto.response.MovimientoCajaResponse;
import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;
import com.sivebo.ms_finanzas.service.MovimientoCajaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Movimientos de Caja", description = "registro de ingresos y egresos de caja")
@RestController
@RequestMapping("api/v1/movimientos")
@RequiredArgsConstructor
public class MovimientoCajaController {

    private final MovimientoCajaService service;

    @Operation(summary = "Registrar movimiento de caja", description = "registra un ingreso o egreso manual en la sesión activa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Movimiento registrado"),
        @ApiResponse(responseCode = "404", description = "Sesión de caja no encontrada")
    })
    @PostMapping
    public ResponseEntity<MovimientoCajaResponse> registrar(@Valid @RequestBody MovimientoCajaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @Operation(summary = "Listar movimientos de una sesión")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos de la sesión")
    @GetMapping("/sesion/{codSesion}")
    public ResponseEntity<List<MovimientoCajaResponse>> listarPorSesion(@PathVariable String codSesion) {
        return ResponseEntity.ok(service.listarPorSesion(codSesion));
    }

    @Operation(summary = "Listar movimientos por tipo", description = "Filtra movimientos de una sesión por INGRESO o EGRESO")
    @ApiResponse(responseCode = "200", description = "Lista filtrada por tipo")
    @GetMapping("/sesion/{codSesion}/tipo")
    public ResponseEntity<List<MovimientoCajaResponse>> listarPorSesionYTipo(@PathVariable String codSesion, @RequestParam TipoMovimiento tipo) {
        return ResponseEntity.ok(service.listarPorSesionYTipo(codSesion, tipo));
    }
}