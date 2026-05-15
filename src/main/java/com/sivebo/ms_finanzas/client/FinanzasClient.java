package com.sivebo.ms_finanzas.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FinanzasClient {

    private static final Logger log = LoggerFactory.getLogger(FinanzasClient.class);
    private final WebClient webClient;

    public FinanzasClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8083").build();
    }

    // Consulta si una venta existe en ms_ventas
    public Boolean verificarVenta(Long idVenta) {
        log.info("Consultando venta id: {} en ms-ventas", idVenta);
        try {
            return webClient.get()
                    .uri("/api/ventas/{id}/existe", idVenta)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al consultar ms-ventas: {}", e.getMessage());
            return false;
        }
    }
}
