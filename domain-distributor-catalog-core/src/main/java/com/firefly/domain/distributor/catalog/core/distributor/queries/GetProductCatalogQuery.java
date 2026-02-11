package com.firefly.domain.distributor.catalog.core.distributor.queries;

import com.firefly.common.cqrs.query.Query;
import com.firefly.core.distributor.sdk.model.ProductDTO;
import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Data
@Builder
public class GetProductCatalogQuery implements Query<Flux<ProductDTO>> {
    private UUID distributorId;

    public GetProductCatalogQuery withDistributorId(UUID distributorId){
        this.distributorId=distributorId;
        return this;
    }

}