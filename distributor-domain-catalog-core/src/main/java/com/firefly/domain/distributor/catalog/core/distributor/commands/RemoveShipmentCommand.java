package com.firefly.domain.distributor.catalog.core.distributor.commands;

import com.firefly.common.cqrs.command.Command;

import java.util.UUID;

public record RemoveShipmentCommand(
        UUID shipmentId
) implements Command<Void>{}