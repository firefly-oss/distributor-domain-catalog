package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingConfigurationApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateLendingConfigurationCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateLendingConfigurationHandler extends CommandHandler<UpdateLendingConfigurationCommand, UUID> {

    private final LendingConfigurationApi lendingConfigurationApi;

    public UpdateLendingConfigurationHandler(LendingConfigurationApi lendingConfigurationApi) {
        this.lendingConfigurationApi = lendingConfigurationApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateLendingConfigurationCommand cmd) {
        return lendingConfigurationApi.updateLendingConfiguration(UUID.randomUUID(), cmd.getProductId(), cmd.getId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(lendingConfigurationDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(lendingConfigurationDTO)).getId());
    }
}