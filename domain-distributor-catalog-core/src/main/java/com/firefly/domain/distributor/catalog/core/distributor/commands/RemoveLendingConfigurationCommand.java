package com.firefly.domain.distributor.catalog.core.distributor.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveLendingConfigurationCommand(
        UUID distributorId,
        UUID productId,
        UUID lendingConfigurationId
) implements Command<Void>{}