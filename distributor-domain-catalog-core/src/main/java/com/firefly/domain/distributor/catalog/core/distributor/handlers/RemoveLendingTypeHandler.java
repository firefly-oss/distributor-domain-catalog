package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.domain.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.domain.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingTypeApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveLendingTypeCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveLendingTypeHandler extends CommandHandler<RemoveLendingTypeCommand, Void> {

    private final LendingTypeApi lendingTypeApi;

    public RemoveLendingTypeHandler(LendingTypeApi lendingTypeApi) {
        this.lendingTypeApi = lendingTypeApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLendingTypeCommand cmd) {
        return lendingTypeApi.deleteLendingType(cmd.lendingTypeId());
    }
}