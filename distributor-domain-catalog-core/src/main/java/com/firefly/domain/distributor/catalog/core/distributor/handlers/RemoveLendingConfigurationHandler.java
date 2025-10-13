package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingConfigurationApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveLendingConfigurationCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveLendingConfigurationHandler extends CommandHandler<RemoveLendingConfigurationCommand, Void> {

    private final LendingConfigurationApi lendingConfigurationApi;

    public RemoveLendingConfigurationHandler(LendingConfigurationApi lendingConfigurationApi) {
        this.lendingConfigurationApi = lendingConfigurationApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLendingConfigurationCommand cmd) {
        return lendingConfigurationApi.deleteLendingConfiguration(cmd.distributorId(), cmd.productId(), cmd.lendingConfigurationId());
    }
}