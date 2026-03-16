package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.ProductApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductInfoCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterProductInfoHandler extends CommandHandler<RegisterProductInfoCommand, UUID> {

    private final ProductApi productApi;

    public RegisterProductInfoHandler(ProductApi productApi) {
        this.productApi = productApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterProductInfoCommand cmd) {
        return productApi.createProduct(cmd.getDistributorId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(applicationCollateralDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(applicationCollateralDTO)).getId());
    }
}