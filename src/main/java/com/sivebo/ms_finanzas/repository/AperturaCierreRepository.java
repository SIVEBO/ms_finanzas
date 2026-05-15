package com.sivebo.ms_finanzas.repository;

import com.sivebo.ms_finanzas.model.entity.AperturaCierre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AperturaCierreRepository extends JpaRepository<AperturaCierre, Long> {
    List<AperturaCierre> findByCaja_IdCaja(Long idCaja);
    Optional<AperturaCierre> findByCaja_IdCajaAndFechaHoraCierreIsNull(Long idCaja);
}
