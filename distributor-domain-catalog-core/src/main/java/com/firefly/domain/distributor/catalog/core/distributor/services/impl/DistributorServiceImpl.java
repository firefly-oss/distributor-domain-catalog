package com.firefly.domain.distributor.catalog.core.distributor.services.impl;

import com.firefly.common.domain.cqrs.query.QueryBus;
import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetProductCatalogQuery;
import com.firefly.domain.distributor.catalog.core.distributor.services.DistributorService;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.RegisterProductSaga;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.UpdateProductSaga;
import com.firefly.transactional.core.SagaResult;
import com.firefly.transactional.engine.SagaEngine;
import com.firefly.transactional.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DistributorServiceImpl implements DistributorService {

    private final SagaEngine engine;
    private final QueryBus queryBus;

    @Autowired
    public DistributorServiceImpl(SagaEngine engine, QueryBus queryBus){
        this.engine=engine;
        this.queryBus = queryBus;
    }

    @Override
    public Mono<SagaResult> registerProduct(UUID distributorId, RegisterProductCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RegisterProductSaga::registerProductCategory, command.getProductCategory())
                .forStep(RegisterProductSaga::registerProduct, command.getProductInfo().withDistributorId(distributorId))
                .forStep(RegisterProductSaga::registerLendingType, command.getLendingType())
                .forStep(RegisterProductSaga::registerLendingConfiguration, command.getLendingConfiguration())
                .forStep(RegisterProductSaga::registerLeasingContract, command.getLeasingContract())
                .forStep(RegisterProductSaga::registerShipment, command.getShipment())

                .build();

        return engine.execute(RegisterProductSaga.class, inputs);
    }

    @Override
    public Mono<Flux<ProductDTO>> listCatalog(UUID distributorId) {
        return queryBus.query(GetProductCatalogQuery.builder().distributorId(distributorId).build());
    }

    @Override
    public Mono<SagaResult> reviseProduct(UUID distributorId, UUID productId, UpdateProductCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateProductSaga::updateProductInfo, command.getProductInfo()
                        .withDistributorId(distributorId)
                        .withId(productId))
                .forStep(UpdateProductSaga::updateLendingConfiguration, command.getLendingConfiguration()
                        .withProductId(productId))
                .forStep(UpdateProductSaga::updateLeasingContract, command.getLeasingContract()
                        .withDistributorId(distributorId)
                        .withProductId(productId))
                .forStep(UpdateProductSaga::updateShipment, command.getShipment()
                        .withProductId(productId))
                .build();

        return engine.execute(UpdateProductSaga.class, inputs);
    }

    @Override
    public Mono<Void> retireProduct(UUID productId) {
        // TODO: Implement retire product ensuring no active contracts are linked
        return Mono.empty();
    }

    @Override
    public Mono<Object> trackProductShipments(UUID productId) {
        // TODO: Implement track shipments associated with a product
        return Mono.empty();
    }

}
