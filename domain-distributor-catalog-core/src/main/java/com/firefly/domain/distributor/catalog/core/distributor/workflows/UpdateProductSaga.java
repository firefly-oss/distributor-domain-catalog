package com.firefly.domain.distributor.catalog.core.distributor.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateLeasingContractCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateLendingConfigurationCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductInfoCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateShipmentCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.distributor.catalog.core.utils.constants.DistributorConstants.*;


@Saga(name = SAGA_UPDATE_PRODUCT)
@Service
public class UpdateProductSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdateProductSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_PRODUCT_INFO)
    @StepEvent(type = EVENT_PRODUCT_INFO_UPDATED)
    public Mono<UUID> updateProductInfo(UpdateProductInfoCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    @SagaStep(id = STEP_UPDATE_LENDING_CONFIGURATION)
    @StepEvent(type = EVENT_LENDING_CONFIGURATION_UPDATED)
    public Mono<UUID> updateLendingConfiguration(UpdateLendingConfigurationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    @SagaStep(id = STEP_UPDATE_LEASING_CONTRACT)
    @StepEvent(type = EVENT_LEASING_CONTRACT_UPDATED)
    public Mono<UUID> updateLeasingContract(UpdateLeasingContractCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    @SagaStep(id = STEP_UPDATE_SHIPMENT)
    @StepEvent(type = EVENT_SHIPMENT_UPDATED)
    public Mono<UUID> updateShipment(UpdateShipmentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

}