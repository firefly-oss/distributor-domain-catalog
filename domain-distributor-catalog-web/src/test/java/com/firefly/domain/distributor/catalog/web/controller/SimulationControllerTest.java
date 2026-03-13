package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.CreateSimulationCommand;
import com.firefly.domain.distributor.catalog.core.distributor.services.SimulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SimulationController}.
 */
@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    @Mock
    private SimulationService simulationService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        SimulationController controller = new SimulationController(simulationService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void createSimulation_shouldReturn201WithSimulationId_whenCreationSucceeds() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        when(simulationService.createSimulation(eq(distributorId), any(CreateSimulationCommand.class)))
                .thenReturn(Mono.just(simulationId));

        webTestClient.post()
                .uri("/api/v1/distributors/{distributorId}/simulations", distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateSimulationCommand())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class)
                .isEqualTo(simulationId);
    }

    @Test
    void createSimulation_shouldPropagateError_whenServiceFails() {
        UUID distributorId = UUID.randomUUID();

        when(simulationService.createSimulation(eq(distributorId), any(CreateSimulationCommand.class)))
                .thenReturn(Mono.error(new RuntimeException("Service failure")));

        webTestClient.post()
                .uri("/api/v1/distributors/{distributorId}/simulations", distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateSimulationCommand())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getSimulation_shouldReturn200WithSimulationDTO_whenFound() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        DistributorSimulationDTO simulationDTO = new DistributorSimulationDTO(simulationId, null, null, null, null);

        when(simulationService.getSimulation(eq(distributorId), eq(simulationId)))
                .thenReturn(Mono.just(simulationDTO));

        webTestClient.get()
                .uri("/api/v1/distributors/{distributorId}/simulations/{simulationId}", distributorId, simulationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DistributorSimulationDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(simulationId));
    }

    @Test
    void getSimulation_shouldPropagateError_whenServiceFails() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        when(simulationService.getSimulation(eq(distributorId), eq(simulationId)))
                .thenReturn(Mono.error(new RuntimeException("Service failure")));

        webTestClient.get()
                .uri("/api/v1/distributors/{distributorId}/simulations/{simulationId}", distributorId, simulationId)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
