package com.firefly.domain.distributor.catalog.core.contract.services;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ContractService {

    Mono<Void> shipContractItem(UUID contractId);

}