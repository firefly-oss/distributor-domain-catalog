package com.firefly.domain.distributor.catalog.infra;

import com.firefly.core.distributor.sdk.api.LendingTypeApi;
import com.firefly.core.distributor.sdk.api.ProductApi;
import com.firefly.core.distributor.sdk.api.ProductCategoryApi;
import com.firefly.core.distributor.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public ClientFactory(
            DistributorCatalogProperties distributorCatalogProperties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(distributorCatalogProperties.getBasePath());
    }

    @Bean
    public ProductApi productApi() {
        return new ProductApi(apiClient);
    }

    @Bean
    public ProductCategoryApi productCategoryApi() {
        return new ProductCategoryApi(apiClient);
    }

    @Bean
    public LendingTypeApi lendingTypeApi() {
        return new LendingTypeApi(apiClient);
    }

}
