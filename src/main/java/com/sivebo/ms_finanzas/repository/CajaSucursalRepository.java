package com.sivebo.ms_finanzas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_finanzas.model.entity.CajaSucursal;

public interface CajaSucursalRepository extends JpaRepository<CajaSucursal, Long> {
    Optional<CajaSucursal> findByIdSucursal(Long idSucursal);
    
}
