//import edu.wpi.first.toolchain.NativePlatforms
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
//    id("edu.wpi.first.GradleRIO")
    id("application")
}

repositories {
    mavenCentral()
}

buildDir = File(rootProject.projectDir, "build/" + project.name)

application {
    mainClassName = "ca.warp7.pathplanner.MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xnew-inference")
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    compile(rootProject)

    // Kotlin Standard Library and Coroutines
    compile(kotlin("stdlib"))

    // WPILib and Vendors
//    wpi.deps.wpilib().forEach { compile(it) }
//    wpi.deps.vendor.java().forEach { compile(it) }
//    wpi.deps.vendor.jni(NativePlatforms.roborio).forEach { nativeZip(it) }
//    wpi.deps.vendor.jni(NativePlatforms.desktop).forEach { nativeDesktopZip(it) }

    compile("org.processing:core:3.3.7")

    // Unit Testing
    testCompile(kotlin("test"))
    testCompile("junit", "junit", "4.12")
}