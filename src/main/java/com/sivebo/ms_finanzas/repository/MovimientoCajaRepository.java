package com.sivebo.ms_finanzas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sivebo.ms_finanzas.model.entity.MovimientoCaja;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findBySesion_IdSesion(Long idSesion);
    List<MovimientoCaja> findBySesion_IdSesionAndTipo(Long idSesion, String tipo);
}
