package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.domain.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.domain.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.ProductApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveProductInfoCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveProductInfoHandler extends CommandHandler<RemoveProductInfoCommand, Void> {

    private final ProductApi productApi;

    public RemoveProductInfoHandler(ProductApi productApi) {
        this.productApi = productApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveProductInfoCommand cmd) {
        return productApi.deleteProduct(cmd.distributorId(), cmd.productId());
    }
}