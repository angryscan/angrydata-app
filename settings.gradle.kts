rootProject.name = "BigDataScanner"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()

    }
}

include(":shared")
include(":desktop")