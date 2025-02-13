import java.io.ByteArrayOutputStream


plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.kotlin.jpa).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.kover)
}

val dummyAttribute = Attribute.of("ru.packetdima", String::class.java)

group = "ru.packetdima"
version = System.getenv("VERSION") ?: "1.0.1"

subprojects {
    group = rootProject.group
    version = rootProject.version


}

dependencies {
    kover(project(":shared"))
    kover(project(":desktop"))
}

tasks.register("getBranch"){
    println(gitBranch)
}
tasks.register("testFiles") {
    println(layout.buildDirectory.file("reports/kover/report.xml").isPresent)
}

fun String.runCommand(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = this@runCommand.split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

val gitBranch = System.getProperty("GIT_BRANCH") ?: "git rev-parse --abbrev-ref HEAD".runCommand()

koverReport{
    filters {
        includes {
            classes("*")
        }
    }
}
