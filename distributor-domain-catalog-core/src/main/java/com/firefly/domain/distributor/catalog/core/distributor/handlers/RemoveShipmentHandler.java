package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.domain.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.domain.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveShipmentCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveShipmentHandler extends CommandHandler<RemoveShipmentCommand, Void> {

    private final ShipmentApi shipmentApi;

    public RemoveShipmentHandler(ShipmentApi shipmentApi) {
        this.shipmentApi = shipmentApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveShipmentCommand cmd) {
        return shipmentApi.deleteShipment(cmd.shipmentId());
    }
}