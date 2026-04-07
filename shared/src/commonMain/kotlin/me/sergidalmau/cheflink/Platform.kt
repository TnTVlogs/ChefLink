package me.sergidalmau.cheflink

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform