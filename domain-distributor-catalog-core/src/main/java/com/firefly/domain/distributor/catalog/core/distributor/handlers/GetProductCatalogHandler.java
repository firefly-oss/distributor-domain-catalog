package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import com.firefly.core.distributor.sdk.api.ProductApi;
import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetProductCatalogQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@QueryHandlerComponent(cacheable = false)
public class GetProductCatalogHandler extends QueryHandler<GetProductCatalogQuery, Flux<ProductDTO>> {

    private final ProductApi productApi;

    public GetProductCatalogHandler(ProductApi productApi) {
        this.productApi = productApi;
    }

    @Override
    protected Mono<Flux<ProductDTO>> doHandle(GetProductCatalogQuery cmd) {
        return Mono.just(productApi.getProductsByDistributorId(cmd.getDistributorId(), null));
    }
}