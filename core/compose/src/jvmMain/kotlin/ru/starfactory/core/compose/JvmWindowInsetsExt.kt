package ru.starfactory.core.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable

@Composable
internal actual fun getSystemBarInsets(): WindowInsets {
    return WindowInsets(0, 0, 0, 0)
}
