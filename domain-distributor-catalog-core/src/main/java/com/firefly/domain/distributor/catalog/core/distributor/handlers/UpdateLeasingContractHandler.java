package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingContractApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateLeasingContractCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdateLeasingContractHandler extends CommandHandler<UpdateLeasingContractCommand, UUID> {

    private final LendingContractApi lendingContractApi;

    public UpdateLeasingContractHandler(LendingContractApi lendingContractApi) {
        this.lendingContractApi = lendingContractApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdateLeasingContractCommand cmd) {
        return lendingContractApi.updateLendingContract(cmd.getId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(leasingContractDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(leasingContractDTO)).getId());
    }
}