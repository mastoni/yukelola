# Architecture Overview

## Principles

1. **Single App** - One application module (`:app`) composing all features
2. **Single Domain** - Shared domain layer (`:core:domain`) with pure Kotlin models
3. **Single Database** - Centralized database module (`:core:database`) with Room
4. **Adaptive UI** - Design system (`:core:designsystem`) for consistent theming
5. **Modular Architecture** - Clear separation between core, feature, and app layers

## Module Structure

```
yukelola
├── :app                          # Android Application
│
├── :core:common                  # Kotlin/JVM - Utilities, extensions
├── :core:domain                  # Kotlin/JVM - Domain models, interfaces
├── :core:database                # Android Library - Room, DAOs, entities
├── :core:designsystem            # Android Library - Theme, components
├── :core:printer                 # Android Library - Printing abstraction
├── :core:license                 # Android Library - License management
│
└── :feature:*
    ├── :feature:home             # Android Library - Home screen
    ├── :feature:pos              # Android Library - Point of Sale
    ├── :feature:products         # Android Library - Product catalog
    ├── :feature:purchase         # Android Library - Purchases
    ├── :feature:customers        # Android Library - Customers
    ├── :feature:suppliers        # Android Library - Suppliers
    ├── :feature:cash             # Android Library - Cash register
    ├── :feature:reports          # Android Library - Reports
    └── :feature:settings         # Android Library - Settings
```

## Package Conventions

- Core: `id.yukelola.core.*`
- Feature: `id.yukelola.feature.*`
- App: `id.yukelola`

## Dependency Direction

```
:app
  ↓
:feature:*
  ↓
:core:*
```

See [DEPENDENCY_RULES.md](DEPENDENCY_RULES.md) for detailed allowed/forbidden dependencies.
See [MODULE_BOUNDARIES.md](MODULE_BOUNDARIES.md) for module responsibilities.