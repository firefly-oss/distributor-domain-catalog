package com.firefly.domain.distributor.catalog.core.distributor.workflows;

import com.firefly.common.domain.cqrs.command.CommandBus;
import com.firefly.domain.distributor.catalog.core.distributor.commands.*;
import com.firefly.transactional.annotations.Saga;
import com.firefly.transactional.annotations.SagaStep;
import com.firefly.transactional.annotations.StepEvent;
import com.firefly.transactional.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.distributor.catalog.core.utils.constants.DistributorConstants.*;
import static com.firefly.domain.distributor.catalog.core.utils.constants.GlobalConstants.*;


@Saga(name = SAGA_REGISTER_PRODUCT)
@Service
public class RegisterProductSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterProductSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }


    @SagaStep(id = STEP_REGISTER_PRODUCT_CATEGORY, compensate = COMPENSATE_REMOVE_PRODUCT_CATEGORY)
    @StepEvent(type = EVENT_PRODUCT_CATEGORY_REGISTERED)
    public Mono<UUID> registerProductCategory(RegisterProductCategoryCommand cmd, SagaContext ctx) {
        return cmd.getId() != null
                ? Mono.<UUID>empty().doFirst(() -> ctx.variables().put(CTX_CATEGORY_ID, cmd.getId()))
                : commandBus.send(cmd).doOnNext(categoryId -> ctx.variables().put(CTX_CATEGORY_ID, categoryId));
    }

    public Mono<Void> removeProductCategory(UUID categoryId) {
        return commandBus.send(new RemoveProductCategoryCommand(categoryId));
    }

    @SagaStep(id = STEP_REGISTER_PRODUCT, compensate = COMPENSATE_REMOVE_PRODUCT, dependsOn = STEP_REGISTER_PRODUCT_CATEGORY)
    @StepEvent(type = EVENT_PRODUCT_REGISTERED)
    public Mono<UUID> registerProduct(RegisterProductInfoCommand cmd, SagaContext ctx) {
        return commandBus.send(cmd.withCategoryId(ctx.getVariableAs(CTX_CATEGORY_ID, UUID.class)))
                .doOnNext(productId -> {
                    ctx.variables().put(CTX_PRODUCT_ID, productId);
                    ctx.variables().put(CTX_DISTRIBUTOR_ID, cmd.getDistributorId());
                });
    }

    public Mono<Void> removeProduct(UUID productId, SagaContext ctx) {
        return commandBus.send(new RemoveProductInfoCommand(ctx.getVariableAs(CTX_DISTRIBUTOR_ID, UUID.class), productId));
    }

    @SagaStep(id = STEP_REGISTER_LENDING_TYPE, compensate = COMPENSATE_REMOVE_LENDING_TYPE)
    @StepEvent(type = EVENT_LENDING_TYPE_REGISTERED)
    public Mono<UUID> registerLendingType(RegisterLendingTypeCommand cmd, SagaContext ctx) {
        return cmd.getId() != null
                ? Mono.<UUID>empty().doFirst(() -> ctx.variables().put(CTX_LENDING_TYPE_ID, cmd.getId()))
                :commandBus.send(cmd)
                .doOnNext(lendingTypeId -> ctx.variables().put(CTX_LENDING_TYPE_ID, lendingTypeId));
    }

    public Mono<Void> removeLendingType(UUID lendingTypeId, SagaContext ctx) {
        return commandBus.send(new RemoveLendingTypeCommand(ctx.getVariableAs(CTX_DISTRIBUTOR_ID, UUID.class), lendingTypeId));
    }

    @SagaStep(id = STEP_REGISTER_LENDING_CONFIGURATION, compensate = COMPENSATE_REMOVE_LENDING_CONFIGURATION, dependsOn = {STEP_REGISTER_LENDING_TYPE, STEP_REGISTER_PRODUCT})
    @StepEvent(type = EVENT_LENDING_CONFIGURATION_REGISTERED)
    public Mono<UUID> registerLendingConfiguration(RegisterLendingConfigurationCommand cmd, SagaContext ctx) {
        return cmd.getId() != null
                ? Mono.<UUID>empty().doFirst(() -> ctx.variables().put(CTX_LENDING_CONFIGURATION_ID, cmd.getId()))
                : commandBus.send(cmd
                        .withProductId(ctx.getVariableAs(CTX_PRODUCT_ID, UUID.class))
                        .withLendingTypeId(ctx.getVariableAs(CTX_LENDING_TYPE_ID, UUID.class)))
                .doOnNext(lendingConfigurationId -> ctx.variables().put(CTX_LENDING_CONFIGURATION_ID, lendingConfigurationId));
    }

    public Mono<Void> removeLendingConfiguration(UUID lendingConfigurationId, SagaContext ctx) {
        return commandBus.send(new RemoveLendingConfigurationCommand(
                ctx.getVariableAs(CTX_DISTRIBUTOR_ID, UUID.class),
                ctx.getVariableAs(CTX_PRODUCT_ID, UUID.class),
                lendingConfigurationId));
    }

    @SagaStep(id = STEP_REGISTER_LEASING_CONTRACT, compensate = COMPENSATE_REMOVE_LEASING_CONTRACT, dependsOn = STEP_REGISTER_LENDING_CONFIGURATION)
    @StepEvent(type = EVENT_LEASING_CONTRACT_REGISTERED)
    public Mono<UUID> registerLeasingContract(RegisterLeasingContractCommand cmd, SagaContext ctx) {
        return cmd.getId() != null
                ? Mono.<UUID>empty().doFirst(() -> ctx.variables().put(CTX_LEASING_CONTRACT_ID, cmd.getId()))
                : commandBus.send(cmd
                        .withDistributorId(ctx.getVariableAs(CTX_DISTRIBUTOR_ID, UUID.class))
                        .withProductId(ctx.getVariableAs(CTX_PRODUCT_ID, UUID.class))
                        .withLendingConfigurationId(ctx.getVariableAs(CTX_LENDING_CONFIGURATION_ID, UUID.class)))
                .doOnNext(leasingContractId -> ctx.variables().put(CTX_LEASING_CONTRACT_ID, leasingContractId));
    }

    public Mono<Void> removeLeasingContract(UUID leasingContractId, SagaContext ctx) {
        return commandBus.send(new RemoveLeasingContractCommand(
                ctx.getVariableAs(CTX_DISTRIBUTOR_ID, UUID.class),
                leasingContractId));
    }

    @SagaStep(id = STEP_REGISTER_SHIPMENT, compensate = COMPENSATE_REMOVE_SHIPMENT, dependsOn = STEP_REGISTER_LEASING_CONTRACT)
    @StepEvent(type = EVENT_SHIPMENT_REGISTERED)
    public Mono<UUID> registerShipment(RegisterShipmentCommand cmd, SagaContext ctx) {
        return cmd.getId() != null
                ? Mono.<UUID>empty().doFirst(() -> ctx.variables().put(CTX_SHIPMENT_ID, cmd.getId()))
                : commandBus.send(cmd
                        .withLeasingContractId(ctx.getVariableAs(CTX_LEASING_CONTRACT_ID, UUID.class))
                        .withProductId(ctx.getVariableAs(CTX_PRODUCT_ID, UUID.class)))
                .doOnNext(shipmentId -> ctx.variables().put(CTX_SHIPMENT_ID, shipmentId));
    }

    public Mono<Void> removeShipment(UUID shipmentId) {
        return commandBus.send(new RemoveShipmentCommand(shipmentId));
    }

}
