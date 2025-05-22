rootProject.name = "BigDataScanner"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.hq.hydraulic.software")
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