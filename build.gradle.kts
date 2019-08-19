@file:Suppress("UnusedImport", "SpellCheckingInspection")

//import edu.wpi.first.gradlerio.GradleRIOPlugin
//import edu.wpi.first.gradlerio.frc.FRCJavaArtifact
//import edu.wpi.first.gradlerio.frc.RoboRIO
//import edu.wpi.first.toolchain.NativePlatforms
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    `maven-publish`
//    id("edu.wpi.first.GradleRIO") version "2019.4.1"
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

group = "ca.warp7.frc"
version = "2019.1.0"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
                "-Xnew-inference",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xallow-kotlin-package",
                "-Xno-call-assertions",
                "-Xno-param-assertions"
        )
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    // Kotlin Standard Library and Coroutines
    compile(kotlin("stdlib"))

    // WPILib and Vendors
//    wpi.deps.wpilib().forEach { compile(it) }
//    wpi.deps.vendor.java().forEach { compile(it) }

    // Unit Testing
    testCompile(kotlin("test"))
    testCompile("junit", "junit", "4.12")
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
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