package com.sivebo.ms_finanzas.repository;

import com.sivebo.ms_finanzas.model.entity.CajaSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CajaSucursalRepository extends JpaRepository<CajaSucursal, Long> {
    Optional<CajaSucursal> findByIdSucursal(Long idSucursal);
    
}
