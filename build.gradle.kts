@file:Suppress("UnusedImport", "SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.50"
    `maven-publish`
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
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {
    }
}

dependencies {
    compile(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.5.1")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.5.1")
}


tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "ca.warp_seven.frc")
    }
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