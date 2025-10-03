package com.firefly.domain.distributor.catalog.core.distributor.services.impl;

import com.firefly.common.domain.cqrs.query.QueryBus;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.services.DistributorService;
import com.firefly.domain.distributor.catalog.core.distributor.workflows.RegisterProductSaga;
import com.firefly.transactional.core.SagaResult;
import com.firefly.transactional.engine.ExpandEach;
import com.firefly.transactional.engine.SagaEngine;
import com.firefly.transactional.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DistributorServiceImpl implements DistributorService {

    private final SagaEngine engine;

    @Autowired
    public DistributorServiceImpl(SagaEngine engine){
        this.engine=engine;
    }

    @Override
    public Mono<SagaResult> registerProduct(UUID distributorId, RegisterProductCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RegisterProductSaga::registerProductCategory, command.getProductCategory())
                .forStep(RegisterProductSaga::registerProduct, command.getProductInfo().withDistributorId(distributorId))
                .build();

        return engine.execute(RegisterProductSaga.class, inputs);
    }
}
