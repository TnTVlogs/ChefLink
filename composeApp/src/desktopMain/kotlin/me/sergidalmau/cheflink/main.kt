package me.sergidalmau.cheflink

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.sergidalmau.cheflink.server.ChefLinkServer
import kotlin.system.exitProcess
import java.io.File
import java.io.PrintStream

fun main() {
    val logDir = File(System.getProperty("user.home"), "ChefLink")
    logDir.mkdirs()
    val logFile = File(logDir, "cheflink.log")
    val logStream = PrintStream(logFile.outputStream(), true)
    System.setOut(logStream)
    System.setErr(logStream)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "ChefLink - Cuina i Control",
            icon = painterResource("icon.ico")
        ) {
            App()
        }
    }
    // application{} returned: all windows closed. Stop server then force JVM exit.
    // System.exit needed because Netty spawns non-daemon threads that block JVM shutdown.
    runCatching { ChefLinkServer.stop() }
    exitProcess(0)
}
