package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LendingContractApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveLeasingContractCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveLeasingContractHandler extends CommandHandler<RemoveLeasingContractCommand, Void> {

    private final LendingContractApi lendingContractApi;

    public RemoveLeasingContractHandler(LendingContractApi lendingContractApi) {
        this.lendingContractApi = lendingContractApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLeasingContractCommand cmd) {
        return lendingContractApi.deleteLendingContract(cmd.leasingContractId(), UUID.randomUUID().toString());
    }
}