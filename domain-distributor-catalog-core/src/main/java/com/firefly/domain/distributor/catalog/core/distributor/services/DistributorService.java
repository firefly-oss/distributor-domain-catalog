package com.firefly.domain.distributor.catalog.core.distributor.services;

import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterShipmentCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductInfoCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.firefly.transactional.saga.core.SagaResult;

import java.util.UUID;

public interface DistributorService {

    /**
     * Registers a new product for a distributor with the specified details.
     *
     * @param distributorId the unique identifier of the distributor for whom the product is being registered
     * @param command the command containing details about the product to be registered, including category,
     *                information, lending type, lending configuration, leasing contract, and shipment
     * @return a {@code Mono<SagaResult>} that indicates the result of the product registration process
     */
    Mono<SagaResult> registerProduct(UUID distributorId, RegisterProductCommand command);

    /**
     * Retrieves the product catalog associated with the specified distributor.
     *
     * @param distributorId the unique identifier of the distributor whose catalog is to be retrieved
     * @return a {@link Mono} emitting a {@link Flux} of {@link ProductDTO} that represents the distributor's catalog
     */
    Mono<Flux<ProductDTO>> listCatalog(UUID distributorId);

    /**
     * Revises the details of an existing product for a given distributor, including updates to
     * its information, lending configuration, leasing contract, and shipment details.
     *
     * @param distributorId the unique identifier of the distributor. Cannot be null.
     * @param productId the unique identifier of the product to be revised. Cannot be null.
     * @param command the {@code UpdateProductCommand} containing the updated product details
     *                and configurations. Cannot be null.
     * @return a {@code Mono<SagaResult>} representing the asynchronous execution result of
     *         the product revision process.
     */
    Mono<SagaResult> reviseProduct(UUID distributorId, UUID productId, UpdateProductCommand command);

    /**
     * Retires a product associated with a specific distributor and updates its information.
     * Ensures that there are no active contracts linked to the product before retiring it.
     *
     * @param distributorId the unique identifier of the distributor
     * @param productId the unique identifier of the product to be retired
     * @param command the command containing updated product information
     * @return a {@code Mono} emitting the result of the operation wrapped in a {@code SagaResult}
     */
    Mono<SagaResult> retireProduct(UUID distributorId, UUID productId,  UpdateProductInfoCommand command);

    /**
     * Tracks all shipments associated with a specific product for a given distributor.
     *
     * @param distributorId the unique identifier of the distributor
     * @param productId the unique identifier of the product
     * @return a reactive Mono containing a Flux stream of ShipmentDTO objects representing the shipments
     */
    Mono<Flux<ShipmentDTO>> trackProductShipments(UUID distributorId, UUID productId);

    /**
     * Registers a shipment for a specific product under a given distributor.
     *
     * @param distributorId the unique identifier of the distributor associated with the shipment
     * @param productId the unique identifier of the product for which the shipment is being registered
     * @param command the command containing details of the shipment to be registered
     * @return a {@code Mono} emitting a {@code SagaResult} representing the outcome of the shipment registration process
     */
    Mono<SagaResult> registerShipment(UUID distributorId, UUID productId, RegisterShipmentCommand command);
}
