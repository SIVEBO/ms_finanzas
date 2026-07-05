package com.sivebo.ms_finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;
import com.sivebo.ms_finanzas.model.enums.TipoMovimiento;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByCodSesion(String codSesion);
    List<MovimientoCaja> findByCodSesionAndTipo(String codSesion, TipoMovimiento tipo);
}
