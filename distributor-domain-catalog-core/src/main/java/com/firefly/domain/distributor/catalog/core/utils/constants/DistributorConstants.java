package com.firefly.domain.distributor.catalog.core.utils.constants;

public class DistributorConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_REGISTER_PRODUCT = "RegisterProductSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_REGISTER_PRODUCT = "registerProduct";
    public static final String STEP_REGISTER_PRODUCT_CATEGORY = "registerProductCategory";
    public static final String STEP_REGISTER_LENDING_TYPE = "registerLendingType";
    public static final String STEP_REGISTER_LENDING_CONFIGURATION = "registerLendingConfiguration";
    public static final String STEP_REGISTER_LEASING_CONTRACT = "registerLeasingContract";


    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_PRODUCT = "removeProduct";
    public static final String COMPENSATE_REMOVE_PRODUCT_CATEGORY = "removeProductCategory";
    public static final String COMPENSATE_REMOVE_LENDING_TYPE = "removeLendingType";
    public static final String COMPENSATE_REMOVE_LENDING_CONFIGURATION = "removeLendingConfiguration";
    public static final String COMPENSATE_REMOVE_LEASING_CONTRACT = "removeLeasingContract";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_PRODUCT_REGISTERED = "product.registered";
    public static final String EVENT_PRODUCT_CATEGORY_REGISTERED = "product.category.registered";
    public static final String EVENT_LENDING_TYPE_REGISTERED = "lending.type.registered";
    public static final String EVENT_LENDING_CONFIGURATION_REGISTERED = "lending.configuration.registered";
    public static final String EVENT_LEASING_CONTRACT_REGISTERED = "leasing.contract.registered";


}
