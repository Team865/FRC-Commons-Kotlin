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
        freeCompilerArgs = listOf("-Xnew-inference")
        kotlinOptions.jvmTarget = "11"
    }
}

dependencies {
    compile(rootProject)

    compile(kotlin("stdlib"))

    compile("org.processing:core:3.3.7")

    // Unit Testing
    testCompile(kotlin("test"))
    testCompile("junit", "junit", "4.12")
}