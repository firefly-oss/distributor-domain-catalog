package com.firefly.domain.distributor.catalog.core.utils.constants;

public class DistributorConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_REGISTER_PRODUCT = "RegisterProductSaga";
    public static final String SAGA_UPDATE_PRODUCT = "UpdateProductSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_REGISTER_PRODUCT = "registerProduct";
    public static final String STEP_REGISTER_PRODUCT_CATEGORY = "registerProductCategory";
    public static final String STEP_REGISTER_LENDING_TYPE = "registerLendingType";
    public static final String STEP_REGISTER_LENDING_CONFIGURATION = "registerLendingConfiguration";
    public static final String STEP_REGISTER_LEASING_CONTRACT = "registerLeasingContract";
    public static final String STEP_REGISTER_SHIPMENT = "registerShipment";
    public static final String STEP_UPDATE_PRODUCT_INFO = "updateProductInfo";
    public static final String STEP_UPDATE_LENDING_CONFIGURATION = "updateLendingConfiguration";
    public static final String STEP_UPDATE_LEASING_CONTRACT = "updateLeasingContract";
    public static final String STEP_UPDATE_SHIPMENT = "updateShipment";

    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_PRODUCT = "removeProduct";
    public static final String COMPENSATE_REMOVE_PRODUCT_CATEGORY = "removeProductCategory";
    public static final String COMPENSATE_REMOVE_LENDING_TYPE = "removeLendingType";
    public static final String COMPENSATE_REMOVE_LENDING_CONFIGURATION = "removeLendingConfiguration";
    public static final String COMPENSATE_REMOVE_LEASING_CONTRACT = "removeLeasingContract";
    public static final String COMPENSATE_REMOVE_SHIPMENT = "removeShipment";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_PRODUCT_REGISTERED = "product.registered";
    public static final String EVENT_PRODUCT_CATEGORY_REGISTERED = "product.category.registered";
    public static final String EVENT_LENDING_TYPE_REGISTERED = "lending.type.registered";
    public static final String EVENT_LENDING_CONFIGURATION_REGISTERED = "lending.configuration.registered";
    public static final String EVENT_LEASING_CONTRACT_REGISTERED = "leasing.contract.registered";
    public static final String EVENT_SHIPMENT_REGISTERED = "shipment.registered";
    public static final String EVENT_PRODUCT_INFO_UPDATED = "productInfo.updated";
    public static final String EVENT_LENDING_CONFIGURATION_UPDATED = "lending.configuration.updated";
    public static final String EVENT_LEASING_CONTRACT_UPDATED = "leasing.contract.updated";
    public static final String EVENT_SHIPMENT_UPDATED = "shipment.updated";


}
