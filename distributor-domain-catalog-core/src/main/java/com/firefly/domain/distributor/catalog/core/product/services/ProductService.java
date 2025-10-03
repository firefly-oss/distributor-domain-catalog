package com.firefly.domain.distributor.catalog.core.product.services;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {

    Mono<Void> reviseProduct(UUID productId);

    Mono<Void> retireProduct(UUID productId);

    Mono<Object> trackProductShipments(UUID productId);

}