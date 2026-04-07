package me.sergidalmau.cheflink.server

fun main() {
    println("Iniciant servidor ChefLink en mode independent...")
    ChefLinkServer.start()
    
    // El servidor Netty corre en pols de fons, però necessitem que el fil principal no mori
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Aturant servidor...")
        ChefLinkServer.stop()
    })
    
    // Mantenim el procés viu
    while (true) {
        Thread.sleep(1000)
    }
}
