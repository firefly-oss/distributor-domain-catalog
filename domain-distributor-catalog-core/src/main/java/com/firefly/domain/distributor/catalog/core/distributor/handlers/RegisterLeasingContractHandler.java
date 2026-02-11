package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LeasingContractApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterLeasingContractCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterLeasingContractHandler extends CommandHandler<RegisterLeasingContractCommand, UUID> {

    private final LeasingContractApi leasingContractApi;

    public RegisterLeasingContractHandler(LeasingContractApi leasingContractApi) {
        this.leasingContractApi = leasingContractApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLeasingContractCommand cmd) {
        return leasingContractApi.createLeasingContract(cmd, UUID.randomUUID().toString())
                .mapNotNull(leasingContractDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(leasingContractDTO)).getId());
    }
}