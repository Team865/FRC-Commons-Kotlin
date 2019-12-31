pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            setUrl("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}

rootProject.name = "FRC-Commons-Kotlin"

include("path-planner")
include("path-planner-2")