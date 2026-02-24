package com.firefly.domain.distributor.catalog.core.distributor.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterShipmentCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveShipmentCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.distributor.catalog.core.utils.constants.DistributorConstants.*;


@Saga(name = SAGA_REGISTER_SHIPMENT)
@Service
public class RegisterShipmentSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterShipmentSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_SHIPMENT, compensate = COMPENSATE_REMOVE_SHIPMENT)
    @StepEvent(type = EVENT_SHIPMENT_REGISTERED)
    public Mono<UUID> registerShipment(RegisterShipmentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> removeShipment(UUID shipmentId) {
        return commandBus.send(new RemoveShipmentCommand(shipmentId));
    }

}
