package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsByContractQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that processes {@link GetShipmentsByContractQuery} by delegating to the
 * shipment API to retrieve shipments associated with a lending contract.
 */
@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent(cacheable = false)
public class GetShipmentsByContractHandler extends QueryHandler<GetShipmentsByContractQuery, ShipmentDTO> {

    private final ShipmentApi shipmentApi;

    @Override
    protected Mono<ShipmentDTO> doHandle(GetShipmentsByContractQuery query) {
        log.debug("Fetching shipments for lending contract id: {}", query.getLendingContractId());
        return shipmentApi.getShipmentsByLendingContractId(
                query.getLendingContractId(), UUID.randomUUID().toString());
    }
}
