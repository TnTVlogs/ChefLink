package me.sergidalmau.cheflink.ui.util

expect class DiscoveryClient() {
    suspend fun discover(): String?
}
