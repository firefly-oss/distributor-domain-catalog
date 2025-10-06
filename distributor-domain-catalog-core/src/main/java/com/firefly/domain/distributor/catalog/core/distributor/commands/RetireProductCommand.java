package com.firefly.domain.distributor.catalog.core.distributor.commands;

import com.firefly.common.domain.cqrs.command.Command;
import com.firefly.core.distributor.sdk.model.ProductDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RetireProductCommand extends ProductDTO implements Command<UUID> {
    private UUID id;
    private UUID distributorId;
    private Boolean isActive;
    private String name;


}