package me.sergidalmau.cheflink.ui.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import me.sergidalmau.cheflink.getPlatform
import kotlin.math.abs

fun Modifier.dragScroll(state: ScrollState): Modifier = composed {
    val isAndroid = remember { getPlatform().name.contains("Android", ignoreCase = true) }
    if (isAndroid) return@composed Modifier
    
    val scope = rememberCoroutineScope()
    Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            scope.launch {
                state.scrollBy(-delta)
            }
        },
        onDragStopped = { velocity ->
            scope.launch {
                if (abs(velocity) > 100f) {
                    val decay = exponentialDecay<Float>(frictionMultiplier = 1.5f)
                    val anim = Animatable(0f)
                    var lastValue = 0f
                    anim.animateDecay(-velocity, decay) {
                        val delta = value - lastValue
                        scope.launch {
                            state.scrollBy(delta)
                        }
                        lastValue = value
                    }
                }
            }
        }
    )
}

fun Modifier.dragScroll(state: LazyListState): Modifier = composed {
    val isAndroid = remember { getPlatform().name.contains("Android", ignoreCase = true) }
    if (isAndroid) return@composed Modifier

    val scope = rememberCoroutineScope()
    Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            scope.launch {
                state.scrollBy(-delta)
            }
        },
        onDragStopped = { velocity ->
            scope.launch {
                if (abs(velocity) > 100f) {
                    val decay = exponentialDecay<Float>(frictionMultiplier = 1.5f)
                    val anim = Animatable(0f)
                    var lastValue = 0f
                    anim.animateDecay(-velocity, decay) {
                        val delta = value - lastValue
                        scope.launch {
                            state.scrollBy(delta)
                        }
                        lastValue = value
                    }
                }
            }
        }
    )
}

fun Modifier.dragScroll(state: LazyGridState): Modifier = composed {
    val isAndroid = remember { getPlatform().name.contains("Android", ignoreCase = true) }
    if (isAndroid) return@composed Modifier

    val scope = rememberCoroutineScope()
    Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            scope.launch {
                state.scrollBy(-delta)
            }
        },
        onDragStopped = { velocity ->
            scope.launch {
                if (abs(velocity) > 100f) {
                    val decay = exponentialDecay<Float>(frictionMultiplier = 1.5f)
                    val anim = Animatable(0f)
                    var lastValue = 0f
                    anim.animateDecay(-velocity, decay) {
                        val delta = value - lastValue
                        scope.launch {
                            state.scrollBy(delta)
                        }
                        lastValue = value
                    }
                }
            }
        }
    )
}
