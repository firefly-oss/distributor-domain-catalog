package com.firefly.domain.distributor.catalog.core.distributor.queries;

import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

/**
 * Query to retrieve shipments associated with a lending contract.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetShipmentsByContractQuery implements Query<ShipmentDTO> {
    private UUID lendingContractId;
}
