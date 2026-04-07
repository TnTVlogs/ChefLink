package me.sergidalmau.cheflink

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.sergidalmau.cheflink.server.ChefLinkServer

fun main() = application {
    Window(
        onCloseRequest = {
            ChefLinkServer.stop() // Aturem el servidor sempre per si de cas
            exitApplication()
        },
        title = "ChefLink - Cuina i Control"
    ) {
        App()
    }
}
