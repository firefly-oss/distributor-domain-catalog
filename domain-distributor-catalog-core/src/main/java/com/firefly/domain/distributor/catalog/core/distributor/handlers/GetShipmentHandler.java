package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that processes {@link GetShipmentQuery} by delegating to the
 * shipment API to retrieve a single shipment by its identifier.
 */
@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent(cacheable = false)
public class GetShipmentHandler extends QueryHandler<GetShipmentQuery, ShipmentDTO> {

    private final ShipmentApi shipmentApi;

    @Override
    protected Mono<ShipmentDTO> doHandle(GetShipmentQuery query) {
        log.debug("Fetching shipment with id: {}", query.getShipmentId());
        return shipmentApi.getShipmentById(query.getShipmentId(), UUID.randomUUID().toString());
    }
}
