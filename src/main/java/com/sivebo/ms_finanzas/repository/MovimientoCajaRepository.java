package com.sivebo.ms_finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findBySesionIdSesion(Long idSesion);
    List<MovimientoCaja> findBySesionIdSesionAndTipo(Long idSesion, String tipo);
}
