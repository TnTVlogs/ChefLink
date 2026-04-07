package me.sergidalmau.cheflink.ui.util

interface ServerManager {
    suspend fun startServer(): Result<Unit>
    fun stopServer()
    fun isServerRunning(): Boolean
}

expect fun getPlatformServerManager(): ServerManager?
