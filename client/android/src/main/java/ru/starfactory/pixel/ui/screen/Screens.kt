package ru.starfactory.pixel.ui.screen

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize
import ru.starfactory.core.navigation.Screen
import ru.starfactory.pixel.ui.screen.settings.SettingsView


@Parcelize
object SettingsScreen : Screen {
    @Composable
    override fun ScreenView() {
        SettingsView()
    }
}
