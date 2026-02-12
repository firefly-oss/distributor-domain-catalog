package com.firefly.domain.distributor.catalog.core.distributor.commands;


import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record RemoveLendingTypeCommand(
        UUID distributorId,
        UUID lendingTypeId
) implements Command<Void> {}