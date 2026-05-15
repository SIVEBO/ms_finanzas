package com.sivebo.ms_finanzas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "caja_sucursal")
public class CajaSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCaja;

    @Column(name = "id_sucursal", nullable = false)
    private Long idSucursal;

    @Column(name = "estado_actual", nullable = false)
    private String estadoActual;
}