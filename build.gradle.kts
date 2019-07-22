@file:Suppress("UnusedImport", "SpellCheckingInspection")

import edu.wpi.first.gradlerio.GradleRIOPlugin
import edu.wpi.first.gradlerio.frc.FRCJavaArtifact
import edu.wpi.first.gradlerio.frc.RoboRIO
import edu.wpi.first.toolchain.NativePlatforms
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    `maven-publish`
    id("edu.wpi.first.GradleRIO") version "2019.4.1"
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xnew-inference")
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    // Kotlin Standard Library and Coroutines
    compile(kotlin("stdlib"))

    // WPILib and Vendors
    wpi.deps.wpilib().forEach { compile(it) }
    wpi.deps.vendor.java().forEach { compile(it) }
    wpi.deps.vendor.jni(NativePlatforms.roborio).forEach { nativeZip(it) }
    wpi.deps.vendor.jni(NativePlatforms.desktop).forEach { nativeDesktopZip(it) }

    // Unit Testing
    testCompile(kotlin("test"))
    testCompile("junit", "junit", "4.12")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveName = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
            artifactId = "frc-commons"
        }
    }
    repositories {
        maven {
            // change to point to your repo, e.g. http://my.org/repo
            url = uri("$buildDir/maven")
        }
    }
}