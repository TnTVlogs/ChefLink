package me.sergidalmau.cheflink.ui.util

import kotlin.math.roundToInt

fun Double.formatPrice(): String {
    val rounded = (this * 100).roundToInt()
    val whole = rounded / 100
    val fraction = rounded % 100
    val fractionStr = if (fraction < 0) (-fraction).toString().padStart(2, '0') 
                      else fraction.toString().padStart(2, '0')
    return "$whole.$fractionStr"
}
