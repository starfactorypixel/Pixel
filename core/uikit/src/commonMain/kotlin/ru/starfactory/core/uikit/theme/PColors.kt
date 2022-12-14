@file:Suppress("UnusedPrivateMember", "MagicNumber") // TODO Sumin
package ru.starfactory.core.uikit.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal const val DEFAULT_CONTENT_ALPHA = .15f

private val LightBlue400 = Color(0xFF29B6F6)
private val Green200 = Color(0xFFA5D6A7)
private val Red800 = Color(0xFFC62828)
private val Grey50 = Color(0xFFFAFAFA)
private val Grey400 = Color(0xFFBDBDBD)
private val BlueGrey600 = Color(0xFF546E7A)
private val BlueGrey900 = Color(0xFF263238)
private val BlueGrey1000 = Color(0xFF1A2227)

internal val LocalPColors = staticCompositionLocalOf<PColors> { error("No value provided") }

@Stable
class PColors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color,

    val border: Color,

    val isLight: Boolean
) {

    fun toColors() = Colors(
        primary = primary,
        primaryVariant = primaryVariant,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onError = onError,
        isLight = isLight,
    )

    companion object {
        val DarkColorPalette = PColors(
            primary = LightBlue400,
            surface = BlueGrey900,
            background = BlueGrey1000,
            onSurface = Grey50,
            onBackground = Grey50,
            error = Red800,

            onPrimary = Color.Magenta,
            primaryVariant = Color.Magenta,
            secondary = Color.Magenta,
            secondaryVariant = Color.Magenta,
            onSecondary = Color.Magenta,
            onError = Color.Magenta,

            border = BlueGrey600,

            isLight = false
        )

        val LightColorPalette = PColors(
            primary = Color.Magenta,
            surface = Color.Magenta,
            background = Color.Magenta,
            onSurface = Color.Magenta,
            onBackground = Color.Magenta,
            onPrimary = Color.Magenta,
            primaryVariant = Color.Magenta,
            secondary = Color.Magenta,
            secondaryVariant = Color.Magenta,
            error = Color.Magenta,
            onSecondary = Color.Magenta,
            onError = Color.Magenta,

            border = Color.Magenta,

            isLight = true
        )
    }
}
