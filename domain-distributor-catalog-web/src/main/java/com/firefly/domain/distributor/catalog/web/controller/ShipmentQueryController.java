package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentQuery;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsByContractQuery;
import com.firefly.domain.distributor.catalog.core.distributor.queries.GetShipmentsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.query.QueryBus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST controller that exposes shipment query and mutation endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Shipment Queries", description = "Query endpoints for shipment look-ups")
public class ShipmentQueryController {

    private final QueryBus queryBus;
    private final ShipmentApi shipmentApi;

    @Operation(summary = "Get shipment by ID", description = "Retrieve a single shipment by its unique identifier.")
    @GetMapping("/catalog/shipments/{shipmentId}")
    public Mono<ResponseEntity<ShipmentDTO>> getShipment(
            @Parameter(description = "Shipment identifier") @PathVariable UUID shipmentId) {
        log.debug("Fetching shipment with id={}", shipmentId);
        return queryBus.<ShipmentDTO>query(GetShipmentQuery.builder()
                        .shipmentId(shipmentId)
                        .build())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get shipments by contract", description = "Retrieve shipments associated with a lending contract.")
    @GetMapping("/catalog/contracts/{contractId}/shipments")
    public Mono<ResponseEntity<ShipmentDTO>> getShipmentsByContract(
            @Parameter(description = "Lending contract identifier") @PathVariable UUID contractId) {
        log.debug("Fetching shipments for contractId={}", contractId);
        return queryBus.<ShipmentDTO>query(GetShipmentsByContractQuery.builder()
                        .lendingContractId(contractId)
                        .build())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List shipments", description = "Retrieve all shipments for a given distributor and product.")
    @GetMapping("/catalog/shipments")
    public Mono<ResponseEntity<Flux<ShipmentDTO>>> listShipments(
            @Parameter(description = "Distributor identifier") @RequestParam UUID distributorId,
            @Parameter(description = "Product identifier") @RequestParam UUID productId) {
        log.debug("Listing shipments for distributorId={}, productId={}", distributorId, productId);
        return queryBus.<Flux<ShipmentDTO>>query(GetShipmentsQuery.builder()
                        .distributorId(distributorId)
                        .productId(productId)
                        .build())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok().build());
    }

    @Operation(summary = "Update shipment", description = "Update an existing shipment.")
    @PutMapping("/catalog/shipments/{shipmentId}")
    public Mono<ResponseEntity<ShipmentDTO>> updateShipment(
            @Parameter(description = "Shipment identifier") @PathVariable UUID shipmentId,
            @Valid @RequestBody ShipmentDTO shipmentDTO) {
        log.debug("Updating shipment with id={}", shipmentId);
        return shipmentApi.updateShipment(shipmentId, shipmentDTO, UUID.randomUUID().toString())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete shipment", description = "Delete a shipment by its identifier.")
    @DeleteMapping("/catalog/shipments/{shipmentId}")
    public Mono<ResponseEntity<Void>> deleteShipment(
            @Parameter(description = "Shipment identifier") @PathVariable UUID shipmentId) {
        log.debug("Deleting shipment with id={}", shipmentId);
        return shipmentApi.deleteShipment(shipmentId, UUID.randomUUID().toString())
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Operation(summary = "Update shipment status", description = "Update the status of a shipment.")
    @PutMapping("/catalog/shipments/{shipmentId}/status")
    public Mono<ResponseEntity<ShipmentDTO>> updateShipmentStatus(
            @Parameter(description = "Shipment identifier") @PathVariable UUID shipmentId,
            @Parameter(description = "New status") @RequestParam String status,
            @Parameter(description = "ID of the user updating the status") @RequestParam(required = false) UUID updatedBy) {
        log.debug("Updating status for shipment with id={} to status={}", shipmentId, status);
        return shipmentApi.updateShipmentStatus(shipmentId, status, updatedBy, UUID.randomUUID().toString())
                .map(ResponseEntity::ok);
    }
}
