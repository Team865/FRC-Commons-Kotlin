import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("application")
    id("org.javamodularity.moduleplugin")
    id("org.openjfx.javafxplugin")
    id ("org.beryx.jlink")
}

repositories {
    mavenCentral()
    jcenter()
}

buildDir = File(rootProject.projectDir, "build/" + project.name)

application {
    mainClassName = "path.planner/ca.warp7.planner2.MainKt"
}

javafx {
    modules("javafx.controls")
}

jlink {
    options.addAll("--strip-debug", "--no-header-files",
            "--no-man-pages", "--strip-native-commands")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        kotlinOptions.jvmTarget = "11"
    }
}

dependencies {
    implementation(rootProject)
    implementation("com.beust:klaxon:5.2")
    implementation(kotlin("stdlib"))
}