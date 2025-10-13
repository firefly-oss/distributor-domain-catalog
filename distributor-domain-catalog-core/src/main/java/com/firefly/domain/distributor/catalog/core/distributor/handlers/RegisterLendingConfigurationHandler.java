package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingConfigurationApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterLendingConfigurationCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterLendingConfigurationHandler extends CommandHandler<RegisterLendingConfigurationCommand, UUID> {

    private final LendingConfigurationApi lendingConfigurationApi;

    public RegisterLendingConfigurationHandler(LendingConfigurationApi lendingConfigurationApi) {
        this.lendingConfigurationApi = lendingConfigurationApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLendingConfigurationCommand cmd) {
        return lendingConfigurationApi.createLendingConfiguration(UUID.randomUUID(), cmd.getProductId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(lendingConfigurationDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(lendingConfigurationDTO)).getId());
    }
}