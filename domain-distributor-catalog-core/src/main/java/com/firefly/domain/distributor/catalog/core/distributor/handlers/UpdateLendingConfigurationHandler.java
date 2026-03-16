package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
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