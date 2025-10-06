package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.domain.cqrs.annotations.QueryHandlerComponent;
import com.firefly.common.domain.cqrs.query.QueryHandler;
import com.firefly.core.distributor.sdk.api.ProductApi;
import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetProductCatalogQuery;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@QueryHandlerComponent(cacheable = false)
public class GetShipmentsHandler extends QueryHandler<GetShipmentsQuery, Flux<ShipmentDTO>> {

    private final ShipmentApi shipmentApi;

    public GetShipmentsHandler(ShipmentApi shipmentApi) {
        this.shipmentApi = shipmentApi;
    }

    @Override
    protected Mono<Flux<ShipmentDTO>> doHandle(GetShipmentsQuery cmd) {
        return Mono.just(shipmentApi.getShipmentsByProductId(cmd.getProductId()));
    }
}