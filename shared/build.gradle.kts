plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
}
kotlin {
    jvm("desktop")
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.desktop.common)
                api(compose.components.resources)
                implementation(compose.materialIconsExtended)

                implementation(libs.dorkbox)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)

                implementation("org.codehaus.plexus:plexus-classworlds:2.8.0")

                implementation("com.github.albfernandez:juniversalchardet:2.4.0")

                implementation(libs.sql.sqlite)
                implementation(libs.sql.postgresql)
                api(libs.exposed.core)
                api(libs.exposed.dao)
                api(libs.exposed.jdbc)

                implementation(libs.datascanner)

                implementation(libs.files.pdfbox)
                implementation(libs.files.fastexcel)
                implementation(libs.files.junrar)
                implementation(libs.files.poi.core)
                implementation(libs.files.poi.ooxml)
                implementation(libs.files.poi.scratchpad)

                implementation(libs.logging.oshai)
                implementation(libs.logging.logback)


            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose.desktop.uiTestJUnit4)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "ru.packetdima.datascanner.resources"
}