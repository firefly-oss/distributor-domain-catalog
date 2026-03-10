package com.firefly.domain.distributor.catalog.core.distributor.queries;

import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

/**
 * Query to retrieve a simulation by distributor and simulation identifiers.
 */
@Data
@Builder
public class GetSimulationQuery implements Query<DistributorSimulationDTO> {
    private UUID distributorId;
    private UUID simulationId;
}
