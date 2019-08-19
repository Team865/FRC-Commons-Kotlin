@file:Suppress("UnusedImport", "SpellCheckingInspection")

plugins {
    `java-library`
    kotlin("jvm") version "1.3.41"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

group = "ca.warp7.frc"
version = "2019.1.1"
//
tasks.compileJava {
    dependsOn(":compileKotlin")
    doFirst {
        options.compilerArgs = listOf("--module-path", classpath.asPath)
        classpath = files()
    }
}
//
tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf(
                "-Xnew-inference",
                "-Xuse-experimental=kotlin.Experimental"//,
//                "-Xallow-kotlin-package",
//                "-Xno-call-assertions",
//                "-Xno-param-assertions"
        )
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.compileKotlin.get().destinationDir = tasks.compileJava.get().destinationDir
//
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation("junit", "junit", "4.12")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
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