import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
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
        kotlinOptions.jvmTarget = "11"
    }
}

dependencies {
    implementation(rootProject)
    implementation(kotlin("stdlib"))
    implementation("org.processing:core:3.3.7")
}