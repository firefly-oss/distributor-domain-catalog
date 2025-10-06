package com.firefly.domain.distributor.catalog.core.distributor.services;

import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterShipmentCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductInfoCommand;
import com.firefly.transactional.core.SagaResult;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DistributorService {

    Mono<SagaResult> registerProduct(UUID distributorId, RegisterProductCommand command);

    Mono<Flux<ProductDTO>> listCatalog(UUID distributorId);

    Mono<SagaResult> reviseProduct(UUID distributorId, UUID productId, UpdateProductCommand command);

    Mono<SagaResult> retireProduct(UUID distributorId, UUID productId,  UpdateProductInfoCommand command);

    Mono<Flux<ShipmentDTO>> trackProductShipments(UUID distributorId, UUID productId);

    Mono<SagaResult> registerShipment(UUID distributorId, UUID productId, RegisterShipmentCommand command);
}
