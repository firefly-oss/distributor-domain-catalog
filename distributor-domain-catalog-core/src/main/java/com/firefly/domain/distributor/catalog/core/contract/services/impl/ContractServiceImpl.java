package com.firefly.domain.distributor.catalog.core.contract.services.impl;

import com.firefly.domain.distributor.catalog.core.contract.services.ContractService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {

    @Override
    public Mono<Void> shipContractItem(UUID contractId) {
        // TODO: Implement register shipment related to a leasing contract
        return Mono.empty();
    }
}