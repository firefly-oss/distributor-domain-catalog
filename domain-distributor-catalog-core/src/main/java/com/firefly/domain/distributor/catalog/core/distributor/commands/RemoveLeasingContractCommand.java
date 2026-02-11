package com.firefly.domain.distributor.catalog.core.distributor.commands;

import com.firefly.common.cqrs.command.Command;

import java.util.UUID;

public record RemoveLeasingContractCommand(
        UUID distributorId,
        UUID leasingContractId
) implements Command<Void>{}