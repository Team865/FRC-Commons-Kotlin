import org.gradle.internal.os.OperatingSystem

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        val frcYear = "2019"
        val frcHome:File
        if (OperatingSystem.current().isWindows) {
            val publicFolder = System.getenv("PUBLIC") ?: "C:\\Users\\Public"
            frcHome = File(publicFolder, "frc$frcYear")
        } else {
            val userFolder = System.getProperty("user.home")
            frcHome = File(userFolder, "frc$frcYear")
        }
        val frcHomeMaven = File(frcHome, "maven")
        maven {
            name = "frcHome"
            url = uri(frcHomeMaven)
        }
    }
}

include("path-planner")