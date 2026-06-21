package com.sivebo.ms_finanzas.controller;

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

import com.sivebo.ms_finanzas.dto.request.CajaSucursalRequest;
import com.sivebo.ms_finanzas.dto.response.CajaSucursalResponse;
import com.sivebo.ms_finanzas.service.CajaSucursalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cajas por Sucursal", description = "Gestión de cajas de cada sucursal (una caja por sucursal)")
@RestController
@RequestMapping("api/v1/cajas")
@RequiredArgsConstructor
public class CajaSucursalController {

    private final CajaSucursalService service;

    @Operation(summary = "Crear caja para sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Caja creada"),
        @ApiResponse(responseCode = "409", description = "La sucursal ya tiene una caja asignada")
    })
    @PostMapping
    public ResponseEntity<CajaSucursalResponse> crear(@Valid @RequestBody CajaSucursalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Listar todas las cajas")
    @ApiResponse(responseCode = "200", description = "Lista de cajas")
    @GetMapping
    public ResponseEntity<List<CajaSucursalResponse>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @Operation(summary = "Obtener caja por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caja encontrada"),
        @ApiResponse(responseCode = "404", description = "Caja no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CajaSucursalResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Obtener caja de una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caja de la sucursal encontrada"),
        @ApiResponse(responseCode = "404", description = "Sucursal sin caja asignada")
    })
    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<CajaSucursalResponse> obtenerPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(service.obtenerPorSucursal(idSucursal));
    }

    @Operation(summary = "Actualizar estado de caja")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Caja no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CajaSucursalResponse> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
    }
}