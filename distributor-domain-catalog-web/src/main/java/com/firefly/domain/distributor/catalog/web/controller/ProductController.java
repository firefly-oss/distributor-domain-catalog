package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.domain.distributor.catalog.core.product.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management operations")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Revise product", description = "Revise product details and financing configurations.")
    @PutMapping(value = "/{productId}")
    public Mono<ResponseEntity<Object>> reviseProduct(@PathVariable UUID productId) {
        return productService.reviseProduct(productId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Retire product", description = "Retire product ensuring no active contracts are linked.")
    @DeleteMapping(value = "/{productId}")
    public Mono<ResponseEntity<Object>> retireProduct(@PathVariable UUID productId) {
        return productService.retireProduct(productId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Track product shipments", description = "Track shipments associated with a product.")
    @GetMapping(value = "/{productId}/shipments")
    public Mono<ResponseEntity<Object>> trackProductShipments(@PathVariable UUID productId) {
        return productService.trackProductShipments(productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok().build());
    }

}