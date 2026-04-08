package me.sergidalmau.cheflink

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.sergidalmau.cheflink.server.ChefLinkServer

fun main() = application {
    Window(
        onCloseRequest = {
            try {
                ChefLinkServer.stop()
            } catch (e: Throwable) {
                println("Error en aturar el servidor: ${e.message}")
            }
            exitApplication()
        },
        title = "ChefLink - Cuina i Control"
    ) {
        App()
    }
}
