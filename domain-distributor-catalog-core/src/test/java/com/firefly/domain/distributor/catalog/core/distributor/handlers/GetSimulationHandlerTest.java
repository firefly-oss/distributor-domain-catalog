package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.DistributorSimulationsApi;
import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetSimulationQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GetSimulationHandler}.
 */
@ExtendWith(MockitoExtension.class)
class GetSimulationHandlerTest {

    @Mock
    private DistributorSimulationsApi distributorSimulationsApi;

    private GetSimulationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetSimulationHandler(distributorSimulationsApi);
    }

    @Test
    void doHandle_shouldReturnSimulationDTO_whenQuerySucceeds() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        GetSimulationQuery query = GetSimulationQuery.builder()
                .distributorId(distributorId)
                .simulationId(simulationId)
                .build();

        DistributorSimulationDTO responseDTO = new DistributorSimulationDTO(
                simulationId, null, null, null, null);

        when(distributorSimulationsApi.getDistributorSimulationById(distributorId, simulationId))
                .thenReturn(Mono.just(responseDTO));

        StepVerifier.create(handler.doHandle(query))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void doHandle_shouldPropagateError_whenApiCallFails() {
        UUID distributorId = UUID.randomUUID();
        UUID simulationId = UUID.randomUUID();

        GetSimulationQuery query = GetSimulationQuery.builder()
                .distributorId(distributorId)
                .simulationId(simulationId)
                .build();

        RuntimeException exception = new RuntimeException("API failure");

        when(distributorSimulationsApi.getDistributorSimulationById(distributorId, simulationId))
                .thenReturn(Mono.error(exception));

        StepVerifier.create(handler.doHandle(query))
                .expectError(RuntimeException.class)
                .verify();
    }
}
