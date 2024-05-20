import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.0-RC3"
    id("org.jetbrains.compose") version "1.6.10-rc03"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0-RC3"
}

dependencies {
    implementation(compose.material3)
    implementation(compose.desktop.currentOs){
        exclude("org.jetbrains.compose.material")
    }
    implementation("io.github.hoc081098:kmp-viewmodel-koin-compose-jvm:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.lazyhat.project_deal.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}
