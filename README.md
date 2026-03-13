# domain-distributor-catalog

> Reactive domain microservice that orchestrates the distributor product catalog, financing structures, leasing contracts, shipments, and lending simulations through CQRS command/query buses and compensatable saga workflows.

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Module Structure](#module-structure)
- [API Endpoints](#api-endpoints)
- [Domain Logic](#domain-logic)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Running Locally](#running-locally)
- [Testing](#testing)

## Overview

`domain-distributor-catalog` is a Spring WebFlux microservice that acts as the domain orchestration layer for distributor product catalogs within the Firefly platform. It sits between client-facing API consumers and the core `core-common-distributor-mgmt` platform service, translating inbound HTTP requests into coordinated multi-step saga workflows that atomically create or mutate catalog entities — product categories, products, lending types, lending configurations, leasing contracts, and shipments — across a distributed system boundary.

The service owns no persistent storage of its own. Every read and write operation is delegated to the upstream `core-common-distributor-mgmt` service through a generated SDK client (`com.firefly.core.distributor.sdk`). State that must flow between saga steps is held transiently in an `ExecutionContext` scoped to a single saga execution. This design keeps the domain layer stateless and horizontally scalable while preserving strong transactional guarantees through compensating rollback actions registered on each saga step.

Domain operations are modelled using the Firefly Framework CQRS and saga-orchestration primitives provided by `fireflyframework-starter-domain`. Commands flow through the `CommandBus` and are dispatched to typed `@CommandHandlerComponent` classes; queries flow through the `QueryBus` and are dispatched to `@QueryHandlerComponent` classes. Complex write workflows — product registration, product revision, product retirement, and standalone shipment registration — are expressed as `@Saga` classes composed of `@SagaStep` methods, which guarantees automatic compensating rollback when any step in the chain fails.

## Architecture

The service follows a layered CQRS and Saga Orchestration pattern:

```
HTTP Client
    |
    v
[domain-distributor-catalog-web]
   Spring WebFlux REST controllers, OpenAPI/Swagger UI, Actuator
    |
    v
[domain-distributor-catalog-interfaces]
   Internal adapter / wiring layer
    |
    v
[domain-distributor-catalog-core]
   Services  -->  SagaEngine  -->  @Saga / @SagaStep
                  CommandBus  -->  @CommandHandlerComponent
                  QueryBus    -->  @QueryHandlerComponent
    |
    v
[domain-distributor-catalog-infra]
   ClientFactory (instantiates SDK API beans)
   DistributorCatalogProperties (binds api-configuration YAML)
    |
    v
[core-common-distributor-mgmt-sdk]
   ProductApi, ProductCategoryApi, LendingTypeApi,
   LendingConfigurationApi, LendingContractApi,
   ShipmentApi, DistributorSimulationsApi
    |
    v (HTTP, default base path: http://localhost:8082)
core-common-distributor-mgmt   (upstream platform microservice)

                          [Kafka]
                     domain-layer topic
                  (step events published by EDA)
```

Each `@SagaStep` annotated with `@StepEvent` publishes a domain event to the configured Kafka topic (`domain-layer`) on successful completion, enabling downstream consumers to react to catalog state changes asynchronously.

## Module Structure

| Module | Purpose |
|--------|---------|
| `domain-distributor-catalog-interfaces` | Internal adapter layer that wires the core module into the web module; contains no independent business logic |
| `domain-distributor-catalog-core` | All business logic: service interfaces and implementations, saga workflow classes, CQRS command/query handlers, domain command and query objects, and shared constants |
| `domain-distributor-catalog-infra` | Infrastructure concerns: `ClientFactory` (constructs and registers upstream `core-common-distributor-mgmt` API client beans) and `DistributorCatalogProperties` (binds `api-configuration.common-platform.distributor-mgmt` YAML properties) |
| `domain-distributor-catalog-web` | Spring Boot application entry point (`DistributorDomainCatalogApplication`), REST controllers (`DistributorController`, `SimulationController`), OpenAPI/Swagger configuration, and Actuator/Prometheus endpoints |
| `domain-distributor-catalog-sdk` | Auto-generated reactive WebClient-based client SDK for this service, produced by the OpenAPI Generator Maven plugin from the service's own OpenAPI spec; intended for consumption by other microservices that need to call this service |

## API Endpoints

### Distributor (`DistributorController`) — tag: `Distributor`

| Method | Path | Description | Success Status |
|--------|------|-------------|---------------|
| `POST` | `/api/v1/distributors/{distributorId}/products` | Register a new product for a distributor, atomically creating its category, product record, lending type, lending configuration, leasing contract, and initial shipment via `RegisterProductSaga` | `200 OK` |
| `GET` | `/api/v1/distributors/{distributorId}/products` | Retrieve the distributor's full product catalog as a reactive `Flux<ProductDTO>` stream | `200 OK` |
| `PUT` | `/api/v1/distributors/{distributorId}/products/{productId}` | Revise product details, lending configuration, leasing contract, and shipment via `UpdateProductSaga` | `200 OK` |
| `DELETE` | `/api/v1/distributors/{distributorId}/products/{productId}` | Retire a product (sets `isActive = false`) via `UpdateProductStatusSaga`; request body carries the product name for audit purposes | `200 OK` |
| `GET` | `/api/v1/distributors/{distributorId}/products/{productId}/shipments` | List all shipments associated with a specific product as a reactive `Flux<ShipmentDTO>` stream | `200 OK` |
| `POST` | `/api/v1/distributors/{distributorId}/products/{productId}/shipments` | Register a new shipment for a product linked to an existing leasing contract via `RegisterShipmentSaga` | `200 OK` |

### Simulations (`SimulationController`) — tag: `Simulations`

| Method | Path | Description | Success Status |
|--------|------|-------------|---------------|
| `POST` | `/api/v1/distributors/{distributorId}/simulations` | Create a new financing simulation for a distributor | `201 Created` |
| `GET` | `/api/v1/distributors/{distributorId}/simulations/{simulationId}` | Retrieve a simulation by its identifier for a given distributor | `200 OK` |

Interactive API documentation is available at `/swagger-ui.html` and the raw OpenAPI specification at `/v3/api-docs` (both are disabled in the `prod` profile).

## Domain Logic

### Sagas

#### `RegisterProductSaga` — invoked by `POST /products`

Orchestrates six saga steps for full product on-boarding. Steps with a `dependsOn` constraint execute only after all declared predecessor steps have completed and stored their results in the shared `ExecutionContext`. Every step that creates a new entity registers a compensating action that the saga engine invokes automatically if a later step fails.

If a `productCategory` or `lendingType` command already carries a non-null `id` (indicating the entity already exists upstream), the corresponding step short-circuits by writing the existing ID into the `ExecutionContext` without calling the downstream API — making those steps idempotent.

| Step ID | Depends On | Event Emitted | Compensating Action |
|---------|-----------|---------------|---------------------|
| `registerProductCategory` | — | `product.category.registered` | `removeProductCategory` |
| `registerProduct` | `registerProductCategory` | `product.registered` | `removeProduct` |
| `registerLendingType` | — | `lending.type.registered` | `removeLendingType` |
| `registerLendingConfiguration` | `registerLendingType`, `registerProduct` | `lending.configuration.registered` | `removeLendingConfiguration` |
| `registerLeasingContract` | `registerLendingConfiguration` | `leasing.contract.registered` | `removeLeasingContract` |
| `registerShipment` | `registerLeasingContract` | `shipment.registered` | `removeShipment` |

#### `UpdateProductSaga` — invoked by `PUT /products/{productId}`

Updates four sub-entities. No `dependsOn` relationships exist between the steps; the saga engine may execute them in any order or in parallel. No compensating actions are defined because updates are considered idempotent.

| Step ID | Event Emitted |
|---------|---------------|
| `updateProductInfo` | `productInfo.updated` |
| `updateLendingConfiguration` | `lending.configuration.updated` |
| `updateLeasingContract` | `leasing.contract.updated` |
| `updateShipment` | `shipment.updated` |

#### `UpdateProductStatusSaga` — invoked by `DELETE /products/{productId}`

Single-step saga. Builds a `RetireProductCommand` with `isActive = false` from the incoming `UpdateProductInfoCommand` and dispatches it via the `CommandBus`.

| Step ID | Event Emitted |
|---------|---------------|
| `updateProduct` | `product.updated` |

#### `RegisterShipmentSaga` — invoked by `POST /products/{productId}/shipments`

Single-step saga with a compensating rollback in case of downstream failure.

| Step ID | Event Emitted | Compensating Action |
|---------|---------------|---------------------|
| `registerShipment` | `shipment.registered` | `removeShipment` |

### Command Handlers

Each command is dispatched by the `CommandBus` to a dedicated `@CommandHandlerComponent`. Handlers are thin delegates that forward the command to the appropriate upstream SDK API client and extract the resulting entity UUID from the response.

| Handler | Command | Upstream API Call |
|---------|---------|-------------------|
| `RegisterProductCategoryHandler` | `RegisterProductCategoryCommand` | `ProductCategoryApi.createProductCategory` |
| `RegisterProductInfoHandler` | `RegisterProductInfoCommand` | `ProductApi.createProduct` |
| `RegisterLendingTypeHandler` | `RegisterLendingTypeCommand` | `LendingTypeApi.createLendingType` |
| `RegisterLendingConfigurationHandler` | `RegisterLendingConfigurationCommand` | `LendingConfigurationApi.createLendingConfiguration` |
| `RegisterLeasingContractHandler` | `RegisterLeasingContractCommand` | `LendingContractApi.createLendingContract` |
| `RegisterShipmentHandler` | `RegisterShipmentCommand` | `ShipmentApi.createShipment` |
| `UpdateProductInfoHandler` | `UpdateProductInfoCommand` | `ProductApi` (update operation) |
| `UpdateLendingConfigurationHandler` | `UpdateLendingConfigurationCommand` | `LendingConfigurationApi` (update operation) |
| `UpdateLeasingContractHandler` | `UpdateLeasingContractCommand` | `LendingContractApi` (update operation) |
| `UpdateShipmentHandler` | `UpdateShipmentCommand` | `ShipmentApi` (update operation) |
| `RetireProductHandler` | `RetireProductCommand` | `ProductApi` (update status) |
| `RemoveProductCategoryHandler` | `RemoveProductCategoryCommand` | `ProductCategoryApi` (delete) |
| `RemoveProductInfoHandler` | `RemoveProductInfoCommand` | `ProductApi` (delete) |
| `RemoveLendingTypeHandler` | `RemoveLendingTypeCommand` | `LendingTypeApi` (delete) |
| `RemoveLendingConfigurationHandler` | `RemoveLendingConfigurationCommand` | `LendingConfigurationApi` (delete) |
| `RemoveLeasingContractHandler` | `RemoveLeasingContractCommand` | `LendingContractApi` (delete) |
| `RemoveShipmentHandler` | `RemoveShipmentCommand` | `ShipmentApi` (delete) |
| `CreateSimulationHandler` | `CreateSimulationCommand` | `DistributorSimulationsApi.createDistributorSimulation` |

### Query Handlers

| Handler | Query | Upstream API Call | Cacheable |
|---------|-------|-------------------|-----------|
| `GetProductCatalogHandler` | `GetProductCatalogQuery` | `ProductApi.getProductsByDistributorId` | No |
| `GetShipmentsHandler` | `GetShipmentsQuery` | `ShipmentApi.getShipmentsByProductId` | No |
| `GetSimulationHandler` | `GetSimulationQuery` | `DistributorSimulationsApi.getDistributorSimulationById` | No |

### Services

| Service Interface | Implementation | Responsibility |
|------------------|----------------|----------------|
| `DistributorService` | `DistributorServiceImpl` | Builds `StepInputs` for each saga and executes them via `SagaEngine`; routes catalog and shipment queries via `QueryBus` |
| `SimulationService` | `SimulationServiceImpl` | Sends `CreateSimulationCommand` via `CommandBus`; routes `GetSimulationQuery` via `QueryBus` |

## Dependencies

### Upstream (consumes)

| Dependency | Maven Artifact | Purpose |
|------------|----------------|---------|
| `core-common-distributor-mgmt` | `core-common-distributor-mgmt-sdk` | Provides all upstream API clients: `ProductApi`, `ProductCategoryApi`, `LendingTypeApi`, `LendingConfigurationApi`, `LendingContractApi`, `ShipmentApi`, `DistributorSimulationsApi` |
| Firefly Framework | `fireflyframework-starter-domain` | CQRS infrastructure (`CommandBus`, `QueryBus`) and saga orchestration engine (`SagaEngine`, `@Saga`, `@SagaStep`, `@StepEvent`) |
| Firefly Framework | `fireflyframework-web` | Common web configuration |
| Firefly Framework | `fireflyframework-utils` | Shared utility helpers |
| Firefly Framework | `fireflyframework-validators` | Validation support |
| Apache Kafka | Configured under `firefly.eda` | Domain event publication to the `domain-layer` topic |

### Downstream (consumed by)

Other services that need to call this service's REST API can import `domain-distributor-catalog-sdk`. The SDK is generated from this service's OpenAPI specification during the Maven build using the `openapi-generator-maven-plugin` with the `webclient` library and reactive response types. It exposes a typed, reactive API client that downstream consumers can wire directly into their own Spring context.

## Configuration

All properties are defined in `domain-distributor-catalog-web/src/main/resources/application.yaml`.

### Core Application

| Property | Default | Description |
|----------|---------|-------------|
| `spring.application.name` | `domain-distributor-catalog` | Service name reported to registries and logs |
| `spring.threads.virtual.enabled` | `true` | Enables Java virtual threads for improved throughput |
| `server.address` | `${SERVER_ADDRESS:localhost}` | Bind address; override with the `SERVER_ADDRESS` environment variable |
| `server.port` | `${SERVER_PORT:8080}` | HTTP port; override with the `SERVER_PORT` environment variable |
| `server.shutdown` | `graceful` | Allows in-flight requests to complete before the process exits |

### Firefly CQRS

| Property | Default | Description |
|----------|---------|-------------|
| `firefly.cqrs.enabled` | `true` | Activates CQRS command and query bus infrastructure |
| `firefly.cqrs.command.timeout` | `30s` | Maximum execution time per command |
| `firefly.cqrs.command.metrics-enabled` | `true` | Exposes command execution metrics via Micrometer |
| `firefly.cqrs.command.tracing-enabled` | `true` | Enables distributed tracing on command execution |
| `firefly.cqrs.query.timeout` | `15s` | Maximum execution time per query |
| `firefly.cqrs.query.caching-enabled` | `true` | Enables query result caching |
| `firefly.cqrs.query.cache-ttl` | `15m` | Time-to-live for cached query results |
| `firefly.saga.performance.enabled` | `true` | Enables saga engine performance instrumentation |

### Firefly EDA (Event-Driven Architecture)

| Property | Default | Description |
|----------|---------|-------------|
| `firefly.eda.enabled` | `true` | Activates the event publishing subsystem |
| `firefly.eda.default-publisher-type` | `KAFKA` | Publisher backend type |
| `firefly.eda.default-connection-id` | `default` | Selects the named Kafka connection configuration |
| `firefly.eda.publishers.kafka.default.enabled` | `true` | Activates the default Kafka publisher bean |
| `firefly.eda.publishers.kafka.default.default-topic` | `domain-layer` | Kafka topic to which domain events are published |
| `firefly.eda.publishers.kafka.default.bootstrap-servers` | `localhost:9092` | Kafka broker address |
| `firefly.stepevents.enabled` | `true` | Enables step-level event emission from saga execution |

### Upstream API Client

| Property | Default | Description |
|----------|---------|-------------|
| `api-configuration.common-platform.distributor-mgmt.base-path` | `http://localhost:8082` | Base URL of the `core-common-distributor-mgmt` service; injected into all SDK API client instances via `ClientFactory` through `DistributorCatalogProperties` |

### Observability

| Property | Value | Description |
|----------|-------|-------------|
| `management.endpoints.web.exposure.include` | `health,info,prometheus` | Actuator endpoints exposed over HTTP |
| `management.endpoint.health.show-details` | `always` | Returns full health component detail in the response body |
| `management.health.livenessState.enabled` | `true` | Enables Kubernetes-compatible liveness probe at `/actuator/health/liveness` |
| `management.health.readinessState.enabled` | `true` | Enables Kubernetes-compatible readiness probe at `/actuator/health/readiness` |
| `management.health.redis.enabled` | `false` | Redis health contributor is disabled |

### OpenAPI / Swagger UI

| Property | Default | Description |
|----------|---------|-------------|
| `springdoc.api-docs.path` | `/v3/api-docs` | Path for the OpenAPI JSON specification |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Path for the Swagger UI |
| `springdoc.packages-to-scan` | `com.firefly.domain.distributor.catalog.web.controller` | Controller packages included in spec generation |
| `springdoc.paths-to-match` | `/api/**` | Path filter applied during spec generation |

### Logging Profiles

| Profile | Log Levels | OpenAPI UI |
|---------|-----------|-----------|
| _(default)_ | Inherits parent defaults | Enabled |
| `dev` | `root=INFO`, `com.firefly=DEBUG`, `org.springframework.r2dbc=DEBUG`, `org.flywaydb=DEBUG` | Enabled |
| `testing` | `root=INFO`, `com.firefly=DEBUG`, `org.springframework.r2dbc=INFO` | Enabled |
| `prod` | `root=WARN`, `com.firefly=INFO`, `org.springframework=WARN` | Disabled |

## Running Locally

Ensure the `core-common-distributor-mgmt` service is reachable at `http://localhost:8082` and a Kafka broker is available at `localhost:9092` before starting.

```bash
mvn clean install -DskipTests
cd /Users/casanchez/Desktop/firefly-oss/domain-distributor-catalog
mvn spring-boot:run -pl domain-distributor-catalog-web
```

Server port: **8080** (default; override with the `SERVER_PORT` environment variable)

To activate the `dev` profile for verbose logging:

```bash
mvn spring-boot:run -pl domain-distributor-catalog-web -Dspring-boot.run.profiles=dev
```

To run the packaged JAR directly:

```bash
java -jar domain-distributor-catalog-web/target/domain-distributor-catalog.jar
```

Once started:
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI spec: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Prometheus metrics: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

## Testing

```bash
mvn clean verify
```

Unit tests for core handlers (`CreateSimulationHandler`, `GetSimulationHandler`) reside in `domain-distributor-catalog-core/src/test`. Controller-level tests for `SimulationController` are in `domain-distributor-catalog-web/src/test`. The test suite uses Spring Boot Test together with Project Reactor's `reactor-test` (`StepVerifier`) for asserting reactive pipelines.

To run tests for a single module:

```bash
mvn test -pl domain-distributor-catalog-core
mvn test -pl domain-distributor-catalog-web
```
