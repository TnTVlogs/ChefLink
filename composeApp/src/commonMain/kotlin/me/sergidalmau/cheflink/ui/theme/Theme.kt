package me.sergidalmau.cheflink.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Modern Professional Palette (Deep Blue & Emerald)
val Primary = Color(0xFF2563EB) // Blue 600 - Vivid but professional
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFDBEAFE) // Blue 100
val OnPrimaryContainer = Color(0xFF1E3A8A) // Blue 900

val Secondary = Color(0xFF475569) // Slate 600
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFF1F5F9) // Slate 100
val OnSecondaryContainer = Color(0xFF0F172A) // Slate 900

val Tertiary = Color(0xFF059669) // Emerald 600 - For success/action accents
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFD1FAE5) // Emerald 100
val OnTertiaryContainer = Color(0xFF064E3B) // Emerald 900

val Error = Color(0xFFDC2626) // Red 600

// We use these globally, making them Composable-friendly
@Composable
fun successColor() = if (MaterialTheme.colorScheme.primary.red < 0.5f) Color(0xFF10B981) else Color(0xFF34D399)
@Composable
fun warningColor() = if (MaterialTheme.colorScheme.primary.red < 0.5f) Color(0xFFD97706) else Color(0xFFFBBF24)

val Background = Color(0xFFF8FAFC) // Slate 50 - Very light gray/blue tint
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1E293B) // Slate 800
val OnSurfaceVariant = Color(0xFF64748B) // Slate 500
val Outline = Color(0xFFCBD5E1) // Slate 300

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = Color.White,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline
)

// Dark scheme - Slate/Night Blue
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA), // Blue 400
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF94A3B8), // Slate 400
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF34D399), // Emerald 400
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFD1FAE5),
    error = Color(0xFFF87171),
    background = Color(0xFF0F172A), // Slate 900
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B), // Slate 800
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        typography = MaterialTheme.typography.copy(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = TextUnit.Unspecified), // keeping default
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp)
        ),
        content = content
    )
}
