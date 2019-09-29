@file:Suppress("UnusedImport", "SpellCheckingInspection")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.41"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.9.18"
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://dl.bintray.com/kotlin/dokka") }
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

dependencies {
    compile(kotlin("stdlib"))

    testCompile(kotlin("test"))
    testCompile("junit", "junit", "4.12")
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

val dokka by tasks.getting(DokkaTask::class)  {
    outputFormat = "html" 
    outputDirectory = "$buildDir/javadoc"
    samples = listOf("src/test/kotlin/test/ca/warp7/frc/geometry")   
}
