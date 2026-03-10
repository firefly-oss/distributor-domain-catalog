package com.firefly.domain.distributor.catalog.core.distributor.services.impl;

import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.CreateSimulationCommand;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetSimulationQuery;
import com.firefly.domain.distributor.catalog.core.distributor.services.SimulationService;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Default implementation of {@link SimulationService} that delegates to the
 * CQRS command and query buses.
 */
@Service
public class SimulationServiceImpl implements SimulationService {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @Autowired
    public SimulationServiceImpl(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @Override
    public Mono<UUID> createSimulation(UUID distributorId, CreateSimulationCommand command) {
        return commandBus.send(command.withDistributorId(distributorId));
    }

    @Override
    public Mono<DistributorSimulationDTO> getSimulation(UUID distributorId, UUID simulationId) {
        return queryBus.query(GetSimulationQuery.builder()
                .distributorId(distributorId)
                .simulationId(simulationId)
                .build());
    }
}
