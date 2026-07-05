package com.sivebo.ms_finanzas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_finanzas.model.entity.AperturaCierre;

public interface AperturaCierreRepository extends JpaRepository<AperturaCierre, Long> {

    List<AperturaCierre> findByNombreSucursal(String nombreSucursal);

    Optional<AperturaCierre> findByNombreSucursalAndFechaHoraCiIsNull(String nombreSucursal);

    Optional<AperturaCierre> findByCodSesion(String codSesion);
}
