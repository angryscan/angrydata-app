import org.gradle.internal.impldep.org.codehaus.plexus.util.Os
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kover)
}

kotlin {
    jvm("desktop")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.logging.oshai)
                implementation(libs.logging.logback)
                implementation(libs.logging.log4j.core)
                implementation(libs.console.progressbar)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.network)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(libs.koin.core)
                implementation(libs.koin.test.junit4)
            }
        }
        // Adds common test dependencies
        commonTest.dependencies {
            implementation(kotlin("test"))

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

compose.desktop {
    application {
        mainClass = "ru.packetdima.datascanner.MainKt"

        jvmArgs += listOf(
            "-Xmx6g"
        )

        buildTypes.release.proguard {
            version.set("7.4.1")
            isEnabled.set(false)
            configurationFiles.from(project.file("compose-desktop.pro"))
            obfuscate.set(true)
        }

        nativeDistributions {
            packageName = "Big Data Scanner"
            vendor = "LLC DETECTICUM"
            packageVersion = version.toString()
            copyright = "© 2024 LLC DETECTICUM"

            modules("java.sql", "jdk.charsets", "jdk.unsupported", "java.naming")

            targetFormats(TargetFormat.Msi, TargetFormat.Deb)

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            windows {
                menuGroup = "start-menu-group"
                installationPath = "Big Data Scanner"
                upgradeUuid = "baf17015-b8d3-4b88-9a59-0031a7b53b34"
                iconFile.set(project(":shared").projectDir.resolve("src\\desktopMain\\composeResources\\files\\icon.ico"))
            }
            linux {
                debMaintainer = "soulofpain.k@gmail.com"
                menuGroup = "Security"
                appCategory = "Utility"
                installationPath = "/opt"
                iconFile.set(project(":shared").projectDir.resolve("src\\desktopMain\\composeResources\\files\\icon.png"))
            }
        }
    }
}

tasks.create("printVersion") {
    doLast {
        print(version)
    }
}

tasks.register("getOS") {
    println(
        "Current OS: ${
            when {
                Os.isFamily(Os.FAMILY_WINDOWS) -> "Windows"
                Os.isFamily(Os.FAMILY_UNIX) -> "Unix"
                Os.isFamily(Os.FAMILY_MAC) -> "MacOS"
                else -> "Unknown"
            }
        }"
    )
}