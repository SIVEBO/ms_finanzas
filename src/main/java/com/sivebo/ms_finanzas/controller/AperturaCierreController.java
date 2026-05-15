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
import com.sivebo.ms_finanzas.service.AperturaCierreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/aperturas")
@RequiredArgsConstructor
public class AperturaCierreController {

    private final AperturaCierreService service;

    @PostMapping("/abrir")
    public ResponseEntity<AperturaCierreResponse> abrirCaja(@Valid @RequestBody AperturaCierreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.abrirCaja(request));
    }

    @PatchMapping("/{idSesion}/cerrar")
    public ResponseEntity<AperturaCierreResponse> cerrarCaja(@PathVariable Long idSesion, @RequestParam BigDecimal montoCierre) {
        return ResponseEntity.ok(service.cerrarCaja(idSesion, montoCierre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AperturaCierreResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<List<AperturaCierreResponse>> listarPorCaja(@PathVariable Long idCaja) {
        return ResponseEntity.ok(service.listarPorCaja(idCaja));
    }

    @GetMapping("/caja/{idCaja}/abierta")
    public ResponseEntity<AperturaCierreResponse> obtenerSesionAbierta(@PathVariable Long idCaja) {
        return ResponseEntity.ok(service.obtenerSesionAbierta(idCaja));
    }
}