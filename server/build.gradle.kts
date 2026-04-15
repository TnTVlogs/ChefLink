plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "me.sergidalmau.cheflink"
version = "1.0.0"

dependencies {
    implementation(projects.shared)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serializationKotlinxJson)
    implementation(libs.ktor.serverWebsockets)
    
    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.migration.core)
    implementation(libs.exposed.migration.jdbc)
    implementation(libs.sqlite.jdbc)
    implementation(libs.mariadb.jdbc)
    implementation(libs.jbcrypt)
    
    // Logging
    implementation(libs.logback.classic)
    implementation(libs.dotenv)
}

application {
    mainClass.set("me.sergidalmau.cheflink.server.MainKt")
}

// Ensure resources are handled
sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}
