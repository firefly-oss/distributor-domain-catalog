package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingTypeApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterLendingTypeCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterLendingTypeHandler extends CommandHandler<RegisterLendingTypeCommand, UUID> {

    private final LendingTypeApi lendingTypeApi;

    public RegisterLendingTypeHandler(LendingTypeApi lendingTypeApi) {
        this.lendingTypeApi = lendingTypeApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLendingTypeCommand cmd) {
        return lendingTypeApi.createLendingType(cmd, UUID.randomUUID().toString())
                .mapNotNull(lendingTypeDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(lendingTypeDTO)).getId());
    }
}