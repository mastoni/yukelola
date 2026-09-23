# Dependency Rules

## Allowed Dependencies

### Application Module
`:app`
- `:feature:home`
- `:feature:pos`
- `:feature:products`
- `:feature:purchase`
- `:feature:customers`
- `:feature:suppliers`
- `:feature:cash`
- `:feature:reports`
- `:feature:settings`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`
- `:core:printer`
- `:core:license`

### Feature Modules
Each feature module may depend on core modules only:

`:feature:home`
- `:core:common`
- `:core:domain`
- `:core:designsystem`

`:feature:pos`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`
- `:core:printer`

`:feature:products`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`

`:feature:purchase`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`

`:feature:customers`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`

`:feature:suppliers`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`

`:feature:cash`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`

`:feature:reports`
- `:core:common`
- `:core:domain`
- `:core:database`
- `:core:designsystem`

`:feature:settings`
- `:core:common`
- `:core:domain`
- `:core:designsystem`
- `:core:license`

### Core Modules
Core modules may depend on other core modules when explicitly necessary:

`:core:common`
- (no internal dependencies)

`:core:domain`
- `:core:common`

`:core:database`
- `:core:common`
- `:core:domain`

`:core:designsystem`
- `:core:common`

`:core:printer`
- `:core:common`
- `:core:domain`

`:core:license`
- `:core:common`
- `:core:domain`

## Forbidden Dependencies

- `:feature:*` → `:feature:*` (any feature-to-feature dependency)
- `:core:*` → `:feature:*` (core depending on feature)
- `:core:*` → `:app` (core depending on app)
- `:app` → (no restriction, app is the root consumer)

## Enforcement

Dependency validation should be enforced via:
- Gradle dependency verification
- Architectural test rules (e.g., ArchUnit or custom Gradle tasks)
- Code review guidelines