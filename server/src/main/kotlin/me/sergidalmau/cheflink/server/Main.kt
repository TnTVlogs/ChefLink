package me.sergidalmau.cheflink.server

fun main() {
    println("Iniciant servidor ChefLink en mode independent...")
    ChefLinkServer.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Aturant servidor...")
        ChefLinkServer.stop()
    })

    while (true) {
        Thread.sleep(1000)
    }
}
