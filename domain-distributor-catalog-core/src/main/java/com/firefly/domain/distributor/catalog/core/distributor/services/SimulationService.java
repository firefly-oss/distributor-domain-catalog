package com.firefly.domain.distributor.catalog.core.distributor.services;

import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.CreateSimulationCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for distributor simulation operations.
 */
public interface SimulationService {

    /**
     * Creates a new simulation for the specified distributor.
     *
     * @param distributorId the unique identifier of the distributor
     * @param command the command containing simulation details
     * @return a {@code Mono} emitting the unique identifier of the created simulation
     */
    Mono<UUID> createSimulation(UUID distributorId, CreateSimulationCommand command);

    /**
     * Retrieves a simulation by its identifier for a given distributor.
     *
     * @param distributorId the unique identifier of the distributor
     * @param simulationId the unique identifier of the simulation
     * @return a {@code Mono} emitting the {@link DistributorSimulationDTO} for the requested simulation
     */
    Mono<DistributorSimulationDTO> getSimulation(UUID distributorId, UUID simulationId);
}
