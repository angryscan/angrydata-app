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
                println("Warning: Conveyor ZIP file not found: ${zipFile.absolutePath}")
                return@forEach
            }

            val extractDir = File(dmgDir, "extract-$arch")
            extractDir.mkdirs()
            
            // Распаковываем ZIP файл
            val unzipResult = ProcessBuilder("unzip", "-q", zipFile.absolutePath, "-d", extractDir.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            if (unzipResult != 0) {
                println("Warning: Failed to extract ZIP file for $arch")
                return@forEach
            }

            val archSuffix = when(arch) {
                "amd64" -> "intel"
                "aarch64" -> "arm64"
                else -> arch
            }
            val dmgFile = File(dmgDir, "big-data-scanner-${version}-mac-${archSuffix}.dmg")
            val appFile = File(extractDir, "Big Data Scanner.app")
            
            if (!appFile.exists()) {
                println("Warning: App file not found after extraction: ${appFile.absolutePath}")
                return@forEach
            }

            // Создаем DMG используя более простой подход без монтирования
            val dmgSize = 300
            val tempDmg = File(dmgDir, "temp-$arch.dmg")

            // Создаем пустой файл DMG
            val ddResult = ProcessBuilder("dd", "if=/dev/zero", "of=${tempDmg.absolutePath}", "bs=1M", "count=$dmgSize")
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            if (ddResult != 0) {
                println("Warning: Failed to create DMG file for $arch")
                return@forEach
            }

            // Форматируем как HFS+
            val mkfsResult = ProcessBuilder("mkfs.hfsplus", "-v", "Big Data Scanner", tempDmg.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            if (mkfsResult != 0) {
                println("Warning: Failed to format DMG file for $arch")
                return@forEach
            }

            // Используем hdiutil для создания DMG (если доступен) или простой подход
            val mountPoint = File(dmgDir, "mnt-$arch")
            mountPoint.mkdirs()
            
            // Пытаемся смонтировать без sudo сначала
            var mountResult = ProcessBuilder("mount", "-o", "loop,rw", tempDmg.absolutePath, mountPoint.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            // Если не получилось, пробуем с sudo
            if (mountResult != 0) {
                mountResult = ProcessBuilder("sudo", "mount", "-o", "loop,rw", tempDmg.absolutePath, mountPoint.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
            }
            
            if (mountResult != 0) {
                println("Warning: Failed to mount DMG file for $arch")
                return@forEach
            }
            
            try {
                // Копируем приложение
                val copyResult = ProcessBuilder("cp", "-R", appFile.absolutePath, mountPoint.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
                
                if (copyResult != 0) {
                    println("Warning: Failed to copy app to DMG for $arch")
                    return@forEach
                }
                
                // Синхронизируем файловую систему
                ProcessBuilder("sync")
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
                
            } finally {
                // Размонтируем
                var umountResult = ProcessBuilder("umount", mountPoint.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
                
                if (umountResult != 0) {
                    umountResult = ProcessBuilder("sudo", "umount", mountPoint.absolutePath)
                        .redirectErrorStream(true)
                        .start()
                        .waitFor()
                }
                
                if (umountResult == 0) {
                    // Переименовываем временный файл в финальный
                    if (tempDmg.renameTo(dmgFile)) {
                        packagesDir.mkdirs()
                        ProcessBuilder("cp", dmgFile.absolutePath, packagesDir.absolutePath)
                            .redirectErrorStream(true)
                            .start()
                            .waitFor()
                        
                        println("DMG created successfully: ${File(packagesDir, dmgFile.name).absolutePath}")
                    } else {
                        println("Warning: Failed to rename DMG file for $arch")
                    }
                } else {
                    println("Warning: Failed to unmount DMG for $arch")
                }
            }
        }
    }
    
    dependsOn("conveyCI")
    outputs.dir(dmgDir)
}

tasks.register("createDMGFromConveyorZIPSimple") {
    val packagesDir = layout.buildDirectory.dir("packages").get().asFile
    val dmgDir = layout.buildDirectory.dir("dmg-output").get().asFile
    
    doFirst {
        dmgDir.mkdirs()
    }
    
    doLast {
        listOf("amd64", "aarch64").forEach { arch ->
            val zipFile = packagesDir.resolve("big-data-scanner-${version}-mac-${arch}.zip")
            if (!zipFile.exists()) {
                println("Warning: Conveyor ZIP file not found: ${zipFile.absolutePath}")
                return@forEach
            }

            val extractDir = File(dmgDir, "extract-$arch")
            extractDir.mkdirs()
            
            // Распаковываем ZIP файл
            val unzipResult = ProcessBuilder("unzip", "-q", zipFile.absolutePath, "-d", extractDir.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            
            if (unzipResult != 0) {
                println("Warning: Failed to extract ZIP file for $arch")
                return@forEach
            }

            val archSuffix = when(arch) {
                "amd64" -> "intel"
                "aarch64" -> "arm64"
                else -> arch
            }
            val dmgFile = File(dmgDir, "big-data-scanner-${version}-mac-${archSuffix}.dmg")
            val appFile = File(extractDir, "Big Data Scanner.app")
            
            if (!appFile.exists()) {
                println("Warning: App file not found after extraction: ${appFile.absolutePath}")
                return@forEach
            }

            // Создаем простой DMG используя genisoimage (если доступен)
            val genisoimageResult = ProcessBuilder(
                "genisoimage", 
                "-D", 
                "-V", "Big Data Scanner",
                "-no-pad",
                "-r",
                "-apple",
                "-o", dmgFile.absolutePath,
                appFile.absolutePath
            ).redirectErrorStream(true)
                .start()
                .waitFor()
            
            if (genisoimageResult == 0) {
                packagesDir.mkdirs()
                ProcessBuilder("cp", dmgFile.absolutePath, packagesDir.absolutePath)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
                
                println("DMG created successfully with genisoimage: ${File(packagesDir, dmgFile.name).absolutePath}")
            } else {
                println("Warning: genisoimage failed for $arch, trying alternative approach")
                
                // Альтернативный подход: создаем ZIP с расширением .dmg
                val alternativeDmg = File(dmgDir, "big-data-scanner-${version}-mac-${archSuffix}-alternative.dmg")
                val zipResult = ProcessBuilder("zip", "-r", alternativeDmg.absolutePath, "Big Data Scanner.app")
                    .directory(extractDir)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
                
                if (zipResult == 0) {
                    packagesDir.mkdirs()
                    ProcessBuilder("cp", alternativeDmg.absolutePath, packagesDir.absolutePath)
                        .redirectErrorStream(true)
                        .start()
                        .waitFor()
                    
                    println("Alternative DMG (ZIP) created: ${File(packagesDir, alternativeDmg.name).absolutePath}")
                } else {
                    println("Warning: Failed to create alternative DMG for $arch")
                }
            }
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