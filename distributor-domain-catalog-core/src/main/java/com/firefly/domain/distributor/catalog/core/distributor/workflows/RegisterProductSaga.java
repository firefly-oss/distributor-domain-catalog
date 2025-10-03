package com.firefly.domain.distributor.catalog.core.distributor.workflows;

import com.firefly.common.domain.cqrs.command.CommandBus;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductInfoCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterProductCategoryCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RegisterLendingTypeCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveProductInfoCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveProductCategoryCommand;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveLendingTypeCommand;
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

}
