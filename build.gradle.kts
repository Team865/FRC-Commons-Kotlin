@file:Suppress("UnusedImport", "SpellCheckingInspection")

import org.javamodularity.moduleplugin.extensions.TestModuleOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.50"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.10.0"
    id("org.javamodularity.moduleplugin") version "1.6.0"
    id("edu.wpi.first.GradleRIO") version "2020.1.1-beta-4"
    id("org.openjfx.javafxplugin") version "0.0.9-SNAPSHOT" apply false
    id("org.beryx.jlink") version "2.16.4" apply false
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://dl.bintray.com/kotlin/dokka") }
}

group = "ca.warp7.frc"
version = "2019.9.0"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
                "-Xno-call-assertions",
                "-Xno-param-assertions"
        )
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.withType<Test> {
    extensions.configure(TestModuleOptions::class.java) {
        runOnClasspath = true
    }
    useJUnitPlatform {
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testImplementation("edu.wpi.first.wpilibj:wpilibj-java:${wpi.wpilibVersion}")
    testImplementation("edu.wpi.first.wpiutil:wpiutil-java:${wpi.wpilibVersion}")
    testImplementation("org.ejml:ejml-simple:${wpi.ejmlVersion}")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:${wpi.jacksonVersion}")
    testImplementation("com.fasterxml.jackson.core:jackson-core:${wpi.jacksonVersion}")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${wpi.jacksonVersion}")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class.java) {
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

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
    configuration {
        moduleName = "ca.warp7.frc"
        noJdkLink = true
        samples = listOf("src/test/kotlin/ca/warp7/frc/geometry")
    }
}
