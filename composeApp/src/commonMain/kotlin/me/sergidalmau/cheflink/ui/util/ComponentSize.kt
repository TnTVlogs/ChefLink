package me.sergidalmau.cheflink.ui.util

enum class ComponentSize(val value: Float) {
    SMALL(0f),
    MEDIUM(1f),
    LARGE(2f);

    companion object {
        fun fromFloat(value: Float): ComponentSize {
            return when {
                value < 0.5f -> SMALL
                value < 1.5f -> MEDIUM
                else -> LARGE
            }
        }
    }
}
