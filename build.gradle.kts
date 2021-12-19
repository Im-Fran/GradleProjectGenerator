import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
}

group = "cl.franciscosolis"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("net.lingala.zip4j:zip4j:2.9.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

compose.desktop {
    application {
        mainClass = "cl.franciscosolis.gradleprojectgenerator.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GradleProjectGenerator"
            packageVersion = project.version.toString()
            description = "A helper to generate your gradle projects."
            copyright = "© 2021 FranciscoSolis. All rights reserved."
            vendor = "FranciscoSolis"
        }
    }
}