package com.firefly.domain.distributor.catalog.core.product.services.impl;

import com.firefly.domain.distributor.catalog.core.product.services.ProductService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Mono<Void> reviseProduct(UUID productId) {
        // TODO: Implement revise product details and financing configurations
        return Mono.empty();
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