plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation(libs.kotlinxSerialization)
}

application {
    // Define the Fully Qualified Name for the application main class.
    // This is the default entry point when running the application.
    mainClass.set("com.sphericalchickens.app.AppKt")

    // Allow overriding the main class from the command line.
    // This is useful for running different solutions without modifying the build file.
    // Example: ./gradlew run -PmainClass=com.sphericalchickens.aoc2015.day11.Day11Kt
    if (project.hasProperty("mainClass")) {
        mainClass.set(project.property("mainClass") as String)
    }
}
