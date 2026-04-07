package me.sergidalmau.cheflink.data.util

import java.io.File
import java.util.Properties

private val propertiesFile = File("cheflink.properties")

actual fun saveLocalData(key: String, value: String) {
    val props = Properties()
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { props.load(it) }
    }
    props.setProperty(key, value)
    propertiesFile.outputStream().use { props.store(it, null) }
}

actual fun getLocalData(key: String): String? {
    if (!propertiesFile.exists()) return null
    val props = Properties()
    propertiesFile.inputStream().use { props.load(it) }
    return props.getProperty(key)
}
