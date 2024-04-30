import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.0-RC1"
    id("org.jetbrains.compose") version "1.6.2"
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("io.github.hoc081098:kmp-viewmodel-koin-compose-jvm:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
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
