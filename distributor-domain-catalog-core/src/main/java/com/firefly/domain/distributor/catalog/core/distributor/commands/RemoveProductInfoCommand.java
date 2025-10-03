package com.firefly.domain.distributor.catalog.core.distributor.commands;

import com.firefly.common.domain.cqrs.command.Command;

import java.util.UUID;

public record RemoveProductInfoCommand(
        UUID distributorId,
        UUID productId
) implements Command<Void>{}