package com.firefly.domain.distributor.catalog.core.distributor.handlers;

import com.firefly.common.cqrs.annotations.CommandHandlerComponent;
import com.firefly.common.cqrs.command.CommandHandler;
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
        return productCategoryApi.deleteProductCategory(cmd.getCategoryId());
    }
}