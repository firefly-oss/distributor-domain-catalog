package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import com.firefly.core.distributor.sdk.api.ProductCategoryApi;
import com.firefly.domain.distributor.catalog.core.distributor.commands.RemoveProductCategoryCommand;
import reactor.core.publisher.Mono;

@CommandHandlerComponent
public class RemoveProductCategoryHandler extends CommandHandler<RemoveProductCategoryCommand, Void> {

    private final ProductCategoryApi productCategoryApi;

    public RemoveProductCategoryHandler(ProductCategoryApi productCategoryApi) {
        this.productCategoryApi = productCategoryApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveProductCategoryCommand cmd) {
        return productCategoryApi.deleteProductCategory(cmd.getCategoryId(), null);
    }
}