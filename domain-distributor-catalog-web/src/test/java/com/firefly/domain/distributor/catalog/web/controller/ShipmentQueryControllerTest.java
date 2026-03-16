package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentQuery;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsByContractQuery;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsQuery;
import org.fireflyframework.cqrs.query.QueryBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ShipmentQueryController}.
 */
@ExtendWith(MockitoExtension.class)
class ShipmentQueryControllerTest {

    @Mock
    private QueryBus queryBus;

    @Mock
    private com.firefly.core.distributor.sdk.api.ShipmentApi shipmentApi;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ShipmentQueryController controller = new ShipmentQueryController(queryBus, shipmentApi);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    // ---- GET /api/v1/catalog/shipments/{shipmentId} ----

    @Test
    void getShipment_shouldReturn200_whenFound() {
        UUID shipmentId = UUID.randomUUID();
        ShipmentDTO dto = new ShipmentDTO();

        when(queryBus.<ShipmentDTO>query(any(GetShipmentQuery.class)))
                .thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/catalog/shipments/{shipmentId}", shipmentId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShipmentDTO.class);
    }

    @Test
    void getShipment_shouldReturn404_whenNotFound() {
        UUID shipmentId = UUID.randomUUID();

        when(queryBus.<ShipmentDTO>query(any(GetShipmentQuery.class)))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/catalog/shipments/{shipmentId}", shipmentId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getShipment_shouldReturn500_whenQueryBusFails() {
        UUID shipmentId = UUID.randomUUID();

        when(queryBus.<ShipmentDTO>query(any(GetShipmentQuery.class)))
                .thenReturn(Mono.error(new RuntimeException("Query bus failure")));

        webTestClient.get()
                .uri("/api/v1/catalog/shipments/{shipmentId}", shipmentId)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ---- GET /api/v1/catalog/contracts/{contractId}/shipments ----

    @Test
    void getShipmentsByContract_shouldReturn200_whenFound() {
        UUID contractId = UUID.randomUUID();
        ShipmentDTO dto = new ShipmentDTO();

        when(queryBus.<ShipmentDTO>query(any(GetShipmentsByContractQuery.class)))
                .thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/catalog/contracts/{contractId}/shipments", contractId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShipmentDTO.class);
    }

    @Test
    void getShipmentsByContract_shouldReturn404_whenNotFound() {
        UUID contractId = UUID.randomUUID();

        when(queryBus.<ShipmentDTO>query(any(GetShipmentsByContractQuery.class)))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/catalog/contracts/{contractId}/shipments", contractId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getShipmentsByContract_shouldReturn500_whenQueryBusFails() {
        UUID contractId = UUID.randomUUID();

        when(queryBus.<ShipmentDTO>query(any(GetShipmentsByContractQuery.class)))
                .thenReturn(Mono.error(new RuntimeException("Query bus failure")));

        webTestClient.get()
                .uri("/api/v1/catalog/contracts/{contractId}/shipments", contractId)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ---- GET /api/v1/catalog/shipments?distributorId=...&productId=... ----

    @Test
    @SuppressWarnings("unchecked")
    void listShipments_shouldReturn200_whenFound() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Flux<ShipmentDTO> shipments = Flux.just(new ShipmentDTO(), new ShipmentDTO());

        when(queryBus.<Flux<ShipmentDTO>>query(any(GetShipmentsQuery.class)))
                .thenReturn(Mono.just(shipments));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalog/shipments")
                        .queryParam("distributorId", distributorId)
                        .queryParam("productId", productId)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listShipments_shouldReturn200Empty_whenNoResults() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(queryBus.<Flux<ShipmentDTO>>query(any(GetShipmentsQuery.class)))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalog/shipments")
                        .queryParam("distributorId", distributorId)
                        .queryParam("productId", productId)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listShipments_shouldReturn500_whenQueryBusFails() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(queryBus.<Flux<ShipmentDTO>>query(any(GetShipmentsQuery.class)))
                .thenReturn(Mono.error(new RuntimeException("Query bus failure")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalog/shipments")
                        .queryParam("distributorId", distributorId)
                        .queryParam("productId", productId)
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
