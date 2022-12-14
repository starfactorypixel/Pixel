package ru.starfactory.pixel.dashboard_screen.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.starfactory.core.compose.LocalConfiguration
import ru.starfactory.pixel.dashboard_screen.ui.widget.CarStatusIndicator
import ru.starfactory.pixel.dashboard_screen.ui.widget.CarStatusView
import ru.starfactory.pixel.dashboard_screen.ui.widget.CurrentSpeedView
import ru.starfactory.pixel.dashboard_screen.ui.widget.FastActionsView
import ru.starfactory.pixel.dashboard_screen.ui.widget.StatisticsView
import ru.starfactory.pixel.main_screen.ui.main_menu_insets.WithLocalMainMenuInsets

@Composable
@Suppress("MagicNumber")
internal fun DashboardTabletContent(
    state: DashboardViewState.ShowData,
) {
    val configuration = LocalConfiguration.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Row {
            CurrentSpeedView(
                state.primaryState.speed,
                Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.weight(1f))
            StatisticsView(
                batteryCharge = state.primaryState.batteryCharge.toInt(),
                Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.weight(1f))

            if (configuration.screenWidth > 600.dp) {
                FastActionsView(Modifier.padding(horizontal = 16.dp))
            }
        }
        WithLocalMainMenuInsets { mainMenuInsets ->
            CarStatusView(

                listOf(
                    CarStatusIndicator(
                        .15f, .5f, Icons.Default.Lock, "Door\nLocked", Color.Green
                    ),
                    CarStatusIndicator(
                        .5f, .20f, Icons.Default.Lock, "Text two\nline", Color.Yellow
                    ),
                    CarStatusIndicator(
                        .85f, .5f, Icons.Default.Lock, "Door\nUnlocked", Color.Red
                    ),
                ),

                Modifier
                    .fillMaxWidth()
                    .padding(start = mainMenuInsets.positionInRoot.x + mainMenuInsets.size.width)
                    .padding(horizontal = 16.dp, vertical = 48.dp)
                    .weight(1f),
            )
        }
    }
}
