import org.gradle.internal.impldep.org.codehaus.plexus.util.Os
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kover)
    alias(libs.plugins.conveyor)
}

kotlin {
    jvm("desktop")
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    sourceSets {
        val desktopMain by getting
        val desktopTest by getting
        commonMain.dependencies {
                implementation(project(":shared"))
                implementation(libs.logging.oshai)
                implementation(libs.logging.logback)
                implementation(libs.logging.log4j.core)
                implementation(libs.console.progressbar)
            }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.network)
        }

        desktopTest.dependencies {
            implementation(compose.desktop.uiTestJUnit4)
            implementation(libs.koin.core)
            implementation(libs.koin.test.junit4)
        }

        // Adds common test dependencies
        commonTest.dependencies {
            implementation(kotlin("test"))

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

dependencies {
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
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
            packageVersion = version.toString()
            copyright = "Open Source Software, 2025"
            licenseFile.set(rootProject.file("LICENSE.en.txt"))

            modules("java.sql", "jdk.charsets", "jdk.unsupported", "java.naming")

            targetFormats(
                TargetFormat.Msi,
                TargetFormat.Deb,
                TargetFormat.Dmg
            )

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            windows {
                menuGroup = "start-menu-group"
                installationPath = "Big Data Scanner"
                upgradeUuid = "baf17015-b8d3-4b88-9a59-0031a7b53b34"
                iconFile.set(project(":shared").projectDir.resolve("src\\desktopMain\\composeResources\\files\\icon.ico"))
                console = true
            }
            linux {
                debMaintainer = "soulofpain.k@gmail.com"
                menuGroup = "Security"
                appCategory = "Utility"
                installationPath = "/opt"
                iconFile.set(project(":shared").projectDir.resolve("src\\desktopMain\\composeResources\\files\\icon.png"))
                modules("jdk.security.auth")
            }
            macOS {
                iconFile.set(project(":shared").projectDir.resolve("src\\desktopMain\\composeResources\\files\\icon.icns"))
                bundleID = "ru.packetdima.datascanner"
                appCategory = "public.app-category.utilities"
            }
        }
    }
}

tasks.register<Exec>("convey") {
    val dir = layout.buildDirectory.dir("packages")
    outputs.dir(dir)
    environment.put("CONVEYOR_AGREE_TO_LICENSE", "1")
    commandLine("conveyor", "make", "--output-dir", dir.get(), "site")
    dependsOn("build", "writeConveyorConfig")
}
tasks.register<Exec>("conveyCI") {
    val dir = layout.buildDirectory.dir("packages")
    outputs.dir(dir)
    environment.put("CONVEYOR_AGREE_TO_LICENSE", "1")
    commandLine("conveyor", "-f", "ci.conveyor.conf", "make", "--output-dir", dir.get(),  "site")
    dependsOn("build", "writeConveyorConfig")
}

tasks.register("printVersion") {
    doLast {
        print(version)
    }
}

tasks.register("getOS") {
    doLast {
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
}

tasks.register("createDMGFromConveyorZIPLinux") {
    val packagesDir = layout.buildDirectory.dir("packages").get().asFile
    val dmgDir = layout.buildDirectory.dir("dmg-output").get().asFile
    
    doFirst {
        dmgDir.mkdirs()
    }
    
    doLast {
        listOf("amd64", "aarch64").forEach { arch ->
            val zipFile = packagesDir.resolve("big-data-scanner-${version}-mac-${arch}.zip")
            if (!zipFile.exists()) {
                throw GradleException("Conveyor ZIP file not found: ${zipFile.absolutePath}")
            }

            val extractDir = File(dmgDir, "extract-$arch")
            extractDir.mkdirs()
            
            // Используем ProcessBuilder вместо exec
            ProcessBuilder("unzip", "-q", zipFile.absolutePath, "-d", extractDir.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()

            val archSuffix = when(arch) {
                "amd64" -> "intel"
                "aarch64" -> "arm64"
                else -> arch
            }
            val dmgFile = File(dmgDir, "big-data-scanner-${version}-mac-${archSuffix}.dmg")
            val appFile = File(extractDir, "Big Data Scanner.app")
            
            if (!appFile.exists()) {
                throw GradleException("App file not found after extraction: ${appFile.absolutePath}")
            }

            val dmgSize = 250
            val tempDmg = File(dmgDir, "temp-$arch.dmg")

            ProcessBuilder("dd", "if=/dev/zero", "of=${tempDmg.absolutePath}", "bs=1M", "count=$dmgSize", "status=none")
                .redirectErrorStream(true)
                .start()
                .waitFor()

            ProcessBuilder("mkfs.hfsplus", "-v", "Big Data Scanner", tempDmg.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()

            val mountPoint = File(dmgDir, "mnt-$arch")
            mountPoint.mkdirs()
            ProcessBuilder("sudo", "mount", "-o", "loop,rw", tempDmg.absolutePath, mountPoint.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            try {
                ProcessBuilder("sudo", "cp", "-R", appFile.absolutePath, mountPoint.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
            } finally {
                ProcessBuilder("sudo", "umount", mountPoint.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
                tempDmg.renameTo(dmgFile)
            }

            packagesDir.mkdirs()
            ProcessBuilder("cp", dmgFile.absolutePath, packagesDir.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            println("DMG created: ${File(packagesDir, dmgFile.name).absolutePath}")
        }
    }
    
    dependsOn("conveyCI")
    outputs.dir(dmgDir)
}

configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}