package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingContractApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterLeasingContractCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterLeasingContractHandler extends CommandHandler<RegisterLeasingContractCommand, UUID> {

    private final LendingContractApi lendingContractApi;

    public RegisterLeasingContractHandler(LendingContractApi lendingContractApi) {
        this.lendingContractApi = lendingContractApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterLeasingContractCommand cmd) {
        return lendingContractApi.createLendingContract(cmd, UUID.randomUUID().toString())
                .mapNotNull(leasingContractDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(leasingContractDTO)).getId());
    }
}