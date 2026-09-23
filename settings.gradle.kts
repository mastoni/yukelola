pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "yukelola"
include(":app")

include(":core:common")
include(":core:domain")
include(":core:database")
include(":core:designsystem")
include(":core:printer")
include(":core:license")

include(":feature:home")
include(":feature:pos")
include(":feature:products")
include(":feature:purchase")
include(":feature:customers")
include(":feature:suppliers")
include(":feature:cash")
include(":feature:reports")
include(":feature:settings")
 