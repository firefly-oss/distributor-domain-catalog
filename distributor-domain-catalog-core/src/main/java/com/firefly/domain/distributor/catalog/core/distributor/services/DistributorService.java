package com.firefly.domain.distributor.catalog.core.distributor.services;

import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCommand;
import com.firefly.transactional.core.SagaResult;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DistributorService {

    Mono<SagaResult> registerProduct(UUID distributorId, @Valid RegisterProductCommand command);

    Mono<Flux<ProductDTO>> listCatalog(UUID distributorId);

}
