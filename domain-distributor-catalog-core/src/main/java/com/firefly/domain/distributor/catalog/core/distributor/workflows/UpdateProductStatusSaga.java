package com.firefly.domain.distributor.catalog.core.distributor.workflows;

import com.firefly.common.cqrs.command.CommandBus;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RetireProductCommand;
import com.firefly.transactional.saga.annotations.Saga;
import com.firefly.transactional.saga.annotations.SagaStep;
import com.firefly.transactional.saga.annotations.StepEvent;
import com.firefly.transactional.saga.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.distributor.catalog.core.utils.constants.DistributorConstants.*;

@Saga(name = SAGA_UPDATE_PRODUCT_STATUS)
@Service
public class UpdateProductStatusSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdateProductStatusSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_PRODUCT)
    @StepEvent(type = EVENT_PRODUCT_UPDATED)
    public Mono<UUID> retireProduct(RetireProductCommand cmd, SagaContext ctx) {
        return commandBus.send(cmd);
    }


}