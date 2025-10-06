package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.core.distributor.sdk.model.ProductDTO;
import com.firefly.core.distributor.sdk.model.ShipmentDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterShipmentCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.UpdateProductInfoCommand;
import com.firefly.domain.distributor.catalog.core.distributor.services.DistributorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/distributors")
@RequiredArgsConstructor
@Tag(name = "Distributor", description = "CQ queries and registration for Distributors")
public class DistributorController {

    private final DistributorService distributorService;

    @Operation(summary = "Submit application", description = "Submit an application with product, amount, currency, and channel.")
    @PostMapping(value = "/{distributorId}/products")
    public Mono<ResponseEntity<Object>> registerProduct(@Valid @RequestBody RegisterProductCommand command, @PathVariable UUID distributorId) {
        return distributorService.registerProduct(distributorId, command)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "List catalog", description = "Retrieve distributor's unified product catalog.")
    @GetMapping(value = "/{distributorId}/products")
    public Mono<ResponseEntity<Flux<ProductDTO>>> listCatalog(@PathVariable UUID distributorId) {
        return distributorService.listCatalog(distributorId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok().build());
    }

    @Operation(summary = "Revise product", description = "Revise product details and financing configurations.")
    @PutMapping(value = "/{distributorId}/products/{productId}")
    public Mono<ResponseEntity<Object>> reviseProduct(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductCommand command) {
        return distributorService.reviseProduct(distributorId, productId, command)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Retire product", description = "Retire product ensuring no active contracts are linked.")
    @DeleteMapping(value = "/{distributorId}/products/{productId}")
    public Mono<ResponseEntity<Object>> retireProduct(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductInfoCommand command) {
        return distributorService.retireProduct(distributorId, productId, command)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Track product shipments", description = "Track shipments associated with a product.")
    @GetMapping(value = "/{distributorId}/products/{productId}/shipments")
    public Mono<ResponseEntity<Flux<ShipmentDTO>>> trackProductShipments(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId) {
        return distributorService.trackProductShipments(distributorId, productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok().build());
    }

    @Operation(summary = "Ship contract item", description = "Register shipment related to a leasing contract.")
    @PostMapping(value = "/{distributorId}/products/{productId}/shipments")
    public Mono<ResponseEntity<Object>> shipContractItem(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId,
            @Valid @RequestBody RegisterShipmentCommand command) {
        return distributorService.registerShipment(distributorId, productId, command)
                .thenReturn(ResponseEntity.ok().build());
    }

}
