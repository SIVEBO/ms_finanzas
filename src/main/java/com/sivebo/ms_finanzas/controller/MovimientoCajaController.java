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
import com.sivebo.ms_finanzas.service.MovimientoCajaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoCajaController {

    private final MovimientoCajaService service;

    @PostMapping
    public ResponseEntity<MovimientoCajaResponse> registrar(@Valid @RequestBody MovimientoCajaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @GetMapping("/sesion/{idSesion}")
    public ResponseEntity<List<MovimientoCajaResponse>> listarPorSesion(@PathVariable Long idSesion) {
        return ResponseEntity.ok(service.listarPorSesion(idSesion));
    }

    @GetMapping("/sesion/{idSesion}/tipo")
    public ResponseEntity<List<MovimientoCajaResponse>> listarPorSesionYTipo(@PathVariable Long idSesion, @RequestParam String tipo) {
        return ResponseEntity.ok(service.listarPorSesionYTipo(idSesion, tipo));
    }
}