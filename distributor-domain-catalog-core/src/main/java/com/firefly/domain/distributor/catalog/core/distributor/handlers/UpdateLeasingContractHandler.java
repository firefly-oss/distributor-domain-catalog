package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.domain.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.domain.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LeasingContractApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateLeasingContractCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateLeasingContractHandler extends CommandHandler<UpdateLeasingContractCommand, UUID> {

    private final LeasingContractApi leasingContractApi;

    public UpdateLeasingContractHandler(LeasingContractApi leasingContractApi) {
        this.leasingContractApi = leasingContractApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateLeasingContractCommand cmd) {
        return leasingContractApi.updateLeasingContract(cmd.getId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(leasingContractDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(leasingContractDTO)).getId());
    }
}