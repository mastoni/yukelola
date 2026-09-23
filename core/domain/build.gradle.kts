plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "id.yukelola.core.domain"
version = "1.0.0"

kotlin {
    jvmToolchain(11)
}

dependencies {
    testImplementation(libs.junit)
}