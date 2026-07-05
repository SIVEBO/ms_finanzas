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
        this.webClient = builder.baseUrl("http://ms-ventas").build();
    }

    public Boolean verificarVenta(Long idVenta) {
        log.info("Consultando venta id: {} en ms-ventas", idVenta);
        try {
            webClient.get()
                    .uri("/api/v1/ventas/{id}", idVenta)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            return true;
        } catch (Exception e) {
            log.error("Error al consultar ms-ventas: {}", e.getMessage());
            return false;
        }
    }
}
