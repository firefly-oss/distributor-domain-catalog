package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.core.distributor.sdk.api.DistributorSimulationsApi;
import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetSimulationQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Handler that processes {@link GetSimulationQuery} by delegating to the
 * distributor simulations API.
 */
@QueryHandlerComponent(cacheable = false)
public class GetSimulationHandler extends QueryHandler<GetSimulationQuery, DistributorSimulationDTO> {

    private final DistributorSimulationsApi distributorSimulationsApi;

    public GetSimulationHandler(DistributorSimulationsApi distributorSimulationsApi) {
        this.distributorSimulationsApi = distributorSimulationsApi;
    }

    @Override
    protected Mono<DistributorSimulationDTO> doHandle(GetSimulationQuery cmd) {
        return distributorSimulationsApi.getDistributorSimulationById(
                cmd.getDistributorId(), cmd.getSimulationId(), UUID.randomUUID().toString());
    }
}
