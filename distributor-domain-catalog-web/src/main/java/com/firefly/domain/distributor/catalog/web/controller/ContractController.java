package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.domain.distributor.catalog.core.contract.services.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract", description = "Contract management operations")
public class ContractController {

    private final ContractService contractService;

    @Operation(summary = "Ship contract item", description = "Register shipment related to a leasing contract.")
    @PostMapping(value = "/{contractId}/shipments")
    public Mono<ResponseEntity<Object>> shipContractItem(@PathVariable UUID contractId) {
        return contractService.shipContractItem(contractId)
                .thenReturn(ResponseEntity.ok().build());
    }

}