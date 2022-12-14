package ru.starfactory.pixel.dashboard_screen.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.starfactory.pixel.dashboard_screen.ui.widget.CurrentSpeedView
import ru.starfactory.pixel.dashboard_screen.ui.widget.FastActionsView
import ru.starfactory.pixel.dashboard_screen.ui.widget.StatisticsView

@Composable
internal fun DashboardPhonePortraitContent(
    state: DashboardViewState.ShowData,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        CurrentSpeedView(
            state.primaryState.speed,
        )
        StatisticsView(
            batteryCharge = state.primaryState.batteryCharge.toInt(),
            Modifier.padding(top = 16.dp)
        )
        FastActionsView(
            Modifier
                .padding(top = 16.dp)
        )
    }
}
