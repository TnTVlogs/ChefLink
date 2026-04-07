package me.sergidalmau.cheflink.ui.util

import me.sergidalmau.cheflink.server.ChefLinkServer

class DesktopServerManager : ServerManager {
    override suspend fun startServer(): Result<Unit> {
        return if (ChefLinkServer.start()) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("No s'ha pogut iniciar el servidor. Revisa si el port 8080 ja està en ús."))
        }
    }

    override fun stopServer() {
        ChefLinkServer.stop()
    }

    override fun isServerRunning(): Boolean {
        return ChefLinkServer.isRunning()
    }
}

actual fun getPlatformServerManager(): ServerManager? = DesktopServerManager()
