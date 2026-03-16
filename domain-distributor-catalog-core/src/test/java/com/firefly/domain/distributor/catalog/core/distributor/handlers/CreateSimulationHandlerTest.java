package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.DistributorSimulationsApi;
import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.CreateSimulationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CreateSimulationHandler}.
 */
@ExtendWith(MockitoExtension.class)
class CreateSimulationHandlerTest {

    @Mock
    private DistributorSimulationsApi distributorSimulationsApi;

    private CreateSimulationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateSimulationHandler(distributorSimulationsApi);
    }

    @Test
    void doHandle_shouldReturnSimulationId_whenCreationSucceeds() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        CreateSimulationCommand command = new CreateSimulationCommand();
        command.withDistributorId(distributorId);

        DistributorSimulationDTO responseDTO = new DistributorSimulationDTO(
                simulationId, null, null, null, null);

        when(distributorSimulationsApi.createDistributorSimulation(eq(distributorId), eq(command), eq(null)))
                .thenReturn(Mono.just(responseDTO));

        StepVerifier.create(handler.doHandle(command))
                .expectNext(simulationId)
                .verifyComplete();
    }

    @Test
    void doHandle_shouldPropagateError_whenApiCallFails() {
        UUID distributorId = UUID.randomUUID();

        CreateSimulationCommand command = new CreateSimulationCommand();
        command.withDistributorId(distributorId);

        RuntimeException exception = new RuntimeException("API failure");

        when(distributorSimulationsApi.createDistributorSimulation(eq(distributorId), eq(command), eq(null)))
                .thenReturn(Mono.error(exception));

        StepVerifier.create(handler.doHandle(command))
                .expectError(RuntimeException.class)
                .verify();
    }
}
