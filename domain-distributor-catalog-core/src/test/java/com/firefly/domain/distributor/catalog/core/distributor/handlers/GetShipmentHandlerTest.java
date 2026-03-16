package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GetShipmentHandler}.
 */
@ExtendWith(MockitoExtension.class)
class GetShipmentHandlerTest {

    @Mock
    private ShipmentApi shipmentApi;

    private GetShipmentHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetShipmentHandler(shipmentApi);
    }

    @Test
    void doHandle_shouldReturnShipmentDTO_whenQuerySucceeds() {
        UUID shipmentId = UUID.randomUUID();

        GetShipmentQuery query = GetShipmentQuery.builder()
                .shipmentId(shipmentId)
                .build();

        ShipmentDTO responseDTO = new ShipmentDTO(shipmentId, null, null, null, null);

        when(shipmentApi.getShipmentById(eq(shipmentId), any(String.class)))
                .thenReturn(Mono.just(responseDTO));

        StepVerifier.create(handler.doHandle(query))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void doHandle_shouldPropagateError_whenApiCallFails() {
        UUID shipmentId = UUID.randomUUID();

        GetShipmentQuery query = GetShipmentQuery.builder()
                .shipmentId(shipmentId)
                .build();

        RuntimeException exception = new RuntimeException("API failure");

        when(shipmentApi.getShipmentById(eq(shipmentId), any(String.class)))
                .thenReturn(Mono.error(exception));

        StepVerifier.create(handler.doHandle(query))
                .expectError(RuntimeException.class)
                .verify();
    }
}
