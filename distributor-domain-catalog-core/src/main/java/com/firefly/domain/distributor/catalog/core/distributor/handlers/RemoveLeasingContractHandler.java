package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.domain.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.domain.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.LeasingContractApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveLeasingContractCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveLeasingContractHandler extends CommandHandler<RemoveLeasingContractCommand, Void> {

    private final LeasingContractApi leasingContractApi;

    public RemoveLeasingContractHandler(LeasingContractApi leasingContractApi) {
        this.leasingContractApi = leasingContractApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveLeasingContractCommand cmd) {
        return leasingContractApi.deleteLeasingContract(cmd.leasingContractId());
    }
}