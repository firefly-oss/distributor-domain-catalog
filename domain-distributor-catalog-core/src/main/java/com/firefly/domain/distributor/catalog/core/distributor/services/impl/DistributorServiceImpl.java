package com.firefly.domain.distributor.catalog.core.distributor.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.*;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetProductCatalogQuery;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsQuery;
import com.firefly.domain.distributor.catalog.core.distributor.services.DistributorService;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.RegisterProductSaga;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.RegisterShipmentSaga;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.UpdateProductSaga;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.UpdateProductStatusSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
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
                .forStepId("registerProductCategory", command.getProductCategory())
                .forStepId("registerProduct", command.getProductInfo().withDistributorId(distributorId))
                .forStepId("registerLendingType", command.getLendingType())
                .forStepId("registerLendingConfiguration", command.getLendingConfiguration())
                .forStepId("registerLeasingContract", command.getLeasingContract())
                .forStepId("registerShipment", command.getShipment())

                .build();

        return engine.execute("RegisterProductSaga", inputs);
    }

    @Override
    public Mono<Flux<ProductDTO>> listCatalog(UUID distributorId) {
        return queryBus.query(GetProductCatalogQuery.builder().distributorId(distributorId).build());
    }

    @Override
    public Mono<SagaResult> reviseProduct(UUID distributorId, UUID productId, UpdateProductCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("updateProductInfo", command.getProductInfo()
                        .withDistributorId(distributorId)
                        .withId(productId))
                .forStepId("updateLendingConfiguration", command.getLendingConfiguration()
                        .withProductId(productId))
                .forStepId("updateLeasingContract", command.getLeasingContract()
                        .withDistributorId(distributorId)
                        .withProductId(productId))
                .forStepId("updateShipment", command.getShipment()
                        .withProductId(productId))
                .build();

        return engine.execute("UpdateProductSaga", inputs);
    }

    @Override
    public Mono<SagaResult> retireProduct(UUID distributorId, UUID productId, UpdateProductInfoCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("updateProduct", RetireProductCommand.builder()
                        .id(productId)
                        .distributorId(distributorId)
                        .name(command.getName())
                        .isActive(false)
                        .build())
                .build();

        return engine.execute("UpdateProductStatusSaga", inputs);
    }

    @Override
    public Mono<Flux<ShipmentDTO>> trackProductShipments(UUID distributorId, UUID productId) {
        return queryBus.query(GetShipmentsQuery.builder().distributorId(distributorId).productId(productId).build());
    }

    @Override
    public Mono<SagaResult> registerShipment(UUID distributorId, UUID productId, RegisterShipmentCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerShipment", command.withProductId(productId))
                .build();

        return engine.execute("RegisterShipmentSaga", inputs);
    }

}
