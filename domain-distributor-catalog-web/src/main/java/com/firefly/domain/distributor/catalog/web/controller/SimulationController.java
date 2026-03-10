package com.firefly.domain.distributor.catalog.web.controller;

import com.firefly.core.distributor.sdk.model.DistributorSimulationDTO;
import com.firefly.domain.distributor.catalog.core.distributor.commands.CreateSimulationCommand;
import com.firefly.domain.distributor.catalog.core.distributor.services.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/distributors/{distributorId}/simulations")
@RequiredArgsConstructor
@Tag(name = "Simulations", description = "Distributor simulation operations")
public class SimulationController {

    private final SimulationService simulationService;

    @Operation(summary = "Create simulation", description = "Create a new simulation for the specified distributor.")
    @PostMapping
    public Mono<ResponseEntity<UUID>> createSimulation(
            @PathVariable UUID distributorId,
            @Valid @RequestBody CreateSimulationCommand command) {
        return simulationService.createSimulation(distributorId, command)
                .map(simulationId -> ResponseEntity.status(HttpStatus.CREATED).body(simulationId));
    }

    @Operation(summary = "Get simulation", description = "Retrieve a simulation by its identifier for a given distributor.")
    @GetMapping(value = "/{simulationId}")
    public Mono<ResponseEntity<DistributorSimulationDTO>> getSimulation(
            @PathVariable UUID distributorId,
            @PathVariable UUID simulationId) {
        return simulationService.getSimulation(distributorId, simulationId)
                .map(ResponseEntity::ok);
    }
}
