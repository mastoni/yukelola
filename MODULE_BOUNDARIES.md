# Module Boundaries

## Core Modules

| Module | Package | Type | Responsibility |
|--------|---------|------|----------------|
| `:core:common` | `id.yukelola.core.common` | Kotlin/JVM | Shared utilities, extensions, constants |
| `:core:domain` | `id.yukelola.core.domain` | Kotlin/JVM | Domain models, repository interfaces, use cases |
| `:core:database` | `id.yukelola.core.database` | Android Library | Room database, DAOs, entities, migrations |
| `:core:designsystem` | `id.yukelola.core.designsystem` | Android Library | Design tokens, theming, shared composables |
| `:core:printer` | `id.yukelola.core.printer` | Android Library | Printing abstraction and implementations |
| `:core:license` | `id.yukelola.core.license` | Android Library | License management and validation |

## Feature Modules

| Module | Package | Type | Responsibility |
|--------|---------|------|----------------|
| `:feature:home` | `id.yukelola.feature.home` | Android Library | Home/dashboard screen |
| `:feature:pos` | `id.yukelola.feature.pos` | Android Library | Point of Sale screen |
| `:feature:products` | `id.yukelola.feature.products` | Android Library | Product catalog management |
| `:feature:purchase` | `id.yukelola.feature.purchase` | Android Library | Purchase order management |
| `:feature:customers` | `id.yukelola.feature.customers` | Android Library | Customer management |
| `:feature:suppliers` | `id.yukelola.feature.suppliers` | Android Library | Supplier management |
| `:feature:cash` | `id.yukelola.feature.cash` | Android Library | Cash register management |
| `:feature:reports` | `id.yukelola.feature.reports` | Android Library | Sales and inventory reports |
| `:feature:settings` | `id.yukelola.feature.settings` | Android Library | Application settings |

## Application Module

| Module | Package | Type | Responsibility |
|--------|---------|------|----------------|
| `:app` | `id.yukelola` | Android Application | App entry point, navigation graph, DI setup |

## Dependency Rules

- `:app` depends on all `:feature:*` and `:core:*`
- `:feature:*` depends on `:core:*` only
- `:feature:*` must NOT depend on other `:feature:*` modules
- `:core:*` must NOT depend on `:feature:*` or `:app`
- `:core:*` may depend on other `:core:*` modules when explicitly necessary