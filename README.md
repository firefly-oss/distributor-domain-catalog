# distributor-domain-catalog

Domain layer microservice responsible for orchestrating distributor product catalog operations. This service acts as the domain orchestration layer between API consumers and the `common-platform-distributor-mgmt` management service, coordinating multi-step product lifecycle workflows through saga-based distributed transactions with CQRS read-side query support.

## Overview

The Distributor Domain Catalog service manages the full lifecycle of distributor product catalogs, lending configurations, leasing contracts, and shipments:

- **Product Registration** -- Orchestrates multi-step product creation including category, product info, lending type, lending configuration, leasing contract, and shipment via a compensatable saga workflow with step dependencies.
- **Product Catalog Retrieval** -- Provides a unified read endpoint to list a distributor's entire product catalog using CQRS query bus.
- **Product Revision** -- Supports updating product details, lending configuration, leasing contract, and shipment information through a coordinated saga.
- **Product Retirement** -- Retires products ensuring no active contracts remain linked, deactivating the product via a status update saga.
- **Shipment Management** -- Registers and tracks shipments associated with products and leasing contracts.
- **Event-Driven Architecture** -- Publishes domain events to Kafka for downstream consumers on every saga step.
- **Saga Orchestration** -- All write operations are executed as saga workflows with compensating transactions, ensuring data consistency across distributed steps.

## Architecture

### Module Structure

| Module | Description |
|--------|-------------|
| `distributor-domain-catalog-core` | Business logic: commands, queries, handlers, saga workflows, service interfaces and implementations |
| `distributor-domain-catalog-interfaces` | Interface adapters connecting core to infrastructure and external boundaries |
| `distributor-domain-catalog-infra` | Infrastructure layer: API client factory, configuration properties, external service integration |
| `distributor-domain-catalog-web` | Spring Boot WebFlux application: REST controllers, application entry point, configuration |
| `distributor-domain-catalog-sdk` | Auto-generated client SDK from OpenAPI spec for downstream consumers |

### Tech Stack

- **Java 25**
- **Spring Boot** with **WebFlux** (reactive, non-blocking) and **virtual threads** enabled
- **[FireflyFramework](https://github.com/fireflyframework/)** -- Parent POM (`fireflyframework-parent`), BOM (`fireflyframework-bom` v26.01.01), and libraries:
  - `fireflyframework-web` -- Common web configurations
  - `fireflyframework-domain` -- Domain layer CQRS and saga support
  - `fireflyframework-utils` -- Shared utilities
  - `fireflyframework-validators` -- Validation framework
- **FireflyFramework Transactional Saga Engine** -- `@Saga`, `@SagaStep`, `@StepEvent` annotations with `SagaEngine` for orchestrating distributed transactions with compensation
- **FireflyFramework CQRS** -- `CommandBus` for command dispatch, `QueryBus` for read-side queries with configurable caching and timeouts
- **FireflyFramework EDA** -- Event-driven architecture with Kafka publisher for domain events
- **Project Reactor** (`Mono`/`Flux`) -- Reactive streams throughout
- **MapStruct** -- Object mapping between layers
- **Lombok** -- Boilerplate reduction
- **SpringDoc OpenAPI** -- API documentation and Swagger UI
- **Micrometer + Prometheus** -- Metrics export
- **Spring Boot Actuator** -- Health checks and operational endpoints
- **OpenAPI Generator** -- SDK generation from the OpenAPI spec (WebClient-based reactive client)

### Saga Workflows

| Saga | Steps | Description |
|------|-------|-------------|
| `RegisterProductSaga` | `registerProductCategory` -> `registerProduct` (depends on category) -> `registerLendingType` -> `registerLendingConfiguration` (depends on lending type + product) -> `registerLeasingContract` (depends on lending config) -> `registerShipment` (depends on leasing contract) | Full product registration with compensating rollback for each step. Steps with existing IDs are skipped (idempotent). |
| `UpdateProductSaga` | `updateProductInfo`, `updateLendingConfiguration`, `updateLeasingContract`, `updateShipment` | Parallel updates to product details and associated configurations |
| `UpdateProductStatusSaga` | `retireProduct` | Deactivates a product by setting `isActive` to false |
| `RegisterShipmentSaga` | `registerShipment` | Standalone shipment registration with compensating removal |

### Domain Events

The service emits the following events via `@StepEvent` and Kafka:

- `product.category.registered`
- `product.registered`
- `lending.type.registered`
- `lending.configuration.registered`
- `leasing.contract.registered`
- `shipment.registered`
- `productInfo.updated`
- `lending.configuration.updated`
- `leasing.contract.updated`
- `shipment.updated`
- `product.updated`

## Setup

### Prerequisites

- **Java 25**
- **Maven 3.9+**
- **Apache Kafka** (default: `localhost:9092`) for event publishing
- Access to the FireflyFramework Maven repository for parent POM and BOM dependencies
- Running instance of `common-platform-distributor-mgmt` service (or its API accessible at the configured base path)

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_ADDRESS` | `localhost` | Server bind address |
| `SERVER_PORT` | `8080` | Server port |

### Application Configuration

Key configuration from `application.yaml`:

```yaml
spring:
  application:
    name: distributor-domain-catalog
  threads:
    virtual:
      enabled: true

firefly:
  cqrs:
    enabled: true
    command:
      timeout: 30s
      metrics-enabled: true
      tracing-enabled: true
    query:
      timeout: 15s
      caching-enabled: true
      cache-ttl: 15m
  saga:
    performance:
      enabled: true
  eda:
    enabled: true
    default-publisher-type: KAFKA
    publishers:
      kafka:
        default:
          default-topic: domain-layer
          bootstrap-servers: localhost:9092
  stepevents:
    enabled: true

api-configuration:
  common-platform.distributor-mgmt:
    base-path: http://localhost:8082
```

### Spring Profiles

| Profile | Behavior |
|---------|----------|
| `dev` | DEBUG logging for `com.firefly`, R2DBC, and Flyway |
| `testing` | DEBUG logging for `com.firefly`; Swagger UI enabled |
| `prod` | WARN-level logging; Swagger UI and API docs disabled |

### Build

```bash
mvn clean install
```

### Run

```bash
mvn -pl distributor-domain-catalog-web spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar distributor-domain-catalog-web/target/distributor-domain-catalog.jar
```

## API Endpoints

Base path: `/api/v1/distributors`

### Register Product

```
POST /api/v1/distributors/{distributorId}/products
```

Registers a new product with category, product info, lending type, lending configuration, leasing contract, and shipment in a single saga transaction. Existing entities (with pre-set IDs) are linked rather than re-created.

**Request Body** (`RegisterProductCommand`):
- `productCategory` -- Category (name, code, description)
- `productInfo` -- Product details (name, description, SKU, model number, manufacturer, image URL, specifications)
- `lendingType` -- Lending type definition (name, code, description)
- `lendingConfiguration` -- Lending terms (min/max/default term months, down payment percentages, interest rate, fees, grace period)
- `leasingContract` -- Contract details (party, dates, payment amounts, approval, terms)
- `shipment` -- Shipment info (tracking number, carrier, address, dates, status)

### List Product Catalog

```
GET /api/v1/distributors/{distributorId}/products
```

Retrieves the distributor's unified product catalog. Returns a reactive stream of `ProductDTO` objects.

### Revise Product

```
PUT /api/v1/distributors/{distributorId}/products/{productId}
```

Revises product details and associated lending configuration, leasing contract, and shipment information.

**Request Body** (`UpdateProductCommand`):
- `productInfo` -- Updated product details
- `lendingConfiguration` -- Updated lending terms
- `leasingContract` -- Updated contract details
- `shipment` -- Updated shipment info

### Retire Product

```
DELETE /api/v1/distributors/{distributorId}/products/{productId}
```

Retires a product by deactivating it, ensuring no active contracts remain linked.

**Request Body** (`UpdateProductInfoCommand`):
- `name` -- Product name (used for reference during retirement)

### Track Product Shipments

```
GET /api/v1/distributors/{distributorId}/products/{productId}/shipments
```

Retrieves all shipments associated with a specific product. Returns a reactive stream of `ShipmentDTO` objects.

### Register Shipment

```
POST /api/v1/distributors/{distributorId}/products/{productId}/shipments
```

Registers a new shipment related to a leasing contract for the specified product.

**Request Body** (`RegisterShipmentCommand`):
- `leasingContractId` -- Associated leasing contract
- `trackingNumber`, `carrier`, `shippingAddress` -- Shipping details
- `shippingDate`, `estimatedDeliveryDate`, `actualDeliveryDate` -- Date tracking
- `status`, `notes` -- Shipment status and notes

### Common Headers

| Header | Required | Description |
|--------|----------|-------------|
| `X-Idempotency-Key` | No | Ensures identical requests are processed only once (write endpoints) |
| `X-Party-ID` | Conditional | Client identifier (at least one identity header required) |
| `X-Employee-ID` | Conditional | Employee identifier |
| `X-Service-Account-ID` | Conditional | Service account identifier |
| `X-Auth-Roles` | No | Comma-separated roles (CUSTOMER, ADMIN, CUSTOMER_SUPPORT, SUPERVISOR, MANAGER, BRANCH_STAFF, SERVICE_ACCOUNT) |
| `X-Auth-Scopes` | No | Comma-separated OAuth2 scopes |
| `X-Request-ID` | No | Request traceability identifier |

## Development Guidelines

- Follow the CQRS pattern: commands for writes dispatched via `CommandBus`, queries for reads dispatched via `QueryBus`
- All write operations must be implemented as saga workflows using `@Saga` and `@SagaStep` annotations
- Define compensating actions for each saga step that mutates state
- Use `@StepEvent` to emit domain events from saga steps (published to Kafka on the `domain-layer` topic)
- Maintain constants in `DistributorConstants` (saga names, step IDs, event types) and `GlobalConstants` (context variable keys)
- Use `SagaContext` to pass variables between dependent saga steps
- For idempotent saga steps, check if the entity already exists (pre-set ID) and skip creation accordingly
- Keep reactive chains unbroken -- return `Mono`/`Flux` throughout the stack
- Use MapStruct for object mapping between layers
- SDK is auto-generated from the OpenAPI spec; do not modify generated code directly
- Infrastructure clients are created via `ClientFactory` using the configured base path
- Query caching is enabled with a 15-minute TTL; consider cache invalidation when data changes

## Monitoring

The service exposes the following operational endpoints via Spring Boot Actuator:

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status with liveness and readiness probes |
| `/actuator/info` | Application information |
| `/actuator/prometheus` | Prometheus metrics endpoint |

Health check details are always shown, with Kubernetes-compatible liveness (`/actuator/health/liveness`) and readiness (`/actuator/health/readiness`) probes enabled. Redis health check is disabled.

OpenAPI documentation is available in `dev` and `testing` profiles:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Swagger UI is configured with alphabetical tag sorting, method-based operation sorting, collapsed sections by default, and filtering enabled.

## Repository

[https://github.com/firefly-oss/distributor-domain-catalog](https://github.com/firefly-oss/distributor-domain-catalog)
