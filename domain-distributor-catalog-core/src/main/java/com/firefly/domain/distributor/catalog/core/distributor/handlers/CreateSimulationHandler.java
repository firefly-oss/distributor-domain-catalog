package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.DistributorSimulationsApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.CreateSimulationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

/**
 * Handler that processes {@link CreateSimulationCommand} by delegating to the
 * distributor simulations API.
 */
@CommandHandlerComponent
public class CreateSimulationHandler extends CommandHandler<CreateSimulationCommand, UUID> {

    private final DistributorSimulationsApi distributorSimulationsApi;

    public CreateSimulationHandler(DistributorSimulationsApi distributorSimulationsApi) {
        this.distributorSimulationsApi = distributorSimulationsApi;
    }

    @Override
    protected Mono<UUID> doHandle(CreateSimulationCommand cmd) {
        return distributorSimulationsApi.createDistributorSimulation(
                        cmd.getDistributorId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(simulationDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(simulationDTO)).getId());
    }
}
