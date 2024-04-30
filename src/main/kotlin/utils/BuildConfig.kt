package utils

import java.io.File
import java.io.FileNotFoundException

private const val jpackageAppVersionPropety = "jpackage.app-version"
private const val osNameProperty = "os.name"

enum class OS {
    Linux, Windows, Unspecified
}

object BuildConfig {
    private val properties = System.getProperties().map { it.key.toString() to it.value.toString() }.toMap()

    //depending on whether it is a build or a simple launch from the IDE, you need to select this var to true or false
    val debug = !properties.containsKey(jpackageAppVersionPropety)

    val appVersion: String = try {
        File("gradle.properties").readText().split('\n').find { it.startsWith("app.version=") }?.substringAfter('=')
            ?: "NOTHING"
    } catch (e: FileNotFoundException) {
        properties.getOrElse(jpackageAppVersionPropety) { "NULL" }
    }

    val os = try {
        OS.valueOf(properties.getOrElse(osNameProperty) { OS.Unspecified.name }.filter { !it.isDigit() }.trim())
    } catch (e: IllegalArgumentException) {
        OS.Unspecified
    }
}