package ru.starfactory.pixel.main_screen.ui.screen.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.replaceCurrent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.starfactory.core.decompose.view_model.ViewModel
import ru.starfactory.core.navigation.NavigationType
import ru.starfactory.core.navigation.createNavigation
import ru.starfactory.core.navigation.getNavigation
import ru.starfactory.feature.apps.domain.AppsFeatureAvailabilityInteractor
import ru.starfactory.feature.apps.ui.screen.AppsScreen
import ru.starfactory.pixel.dashboard_screen.ui.screen.DashboardScreen
import ru.starfactory.pixel.main_screen.ui.screen.ChargingScreen
import ru.starfactory.pixel.main_screen.ui.screen.NavigatorScreen

internal class MainViewModel(
    private val appsFeatureAvailabilityInteractor: AppsFeatureAvailabilityInteractor,
    componentContext: ComponentContext,
) : ViewModel() {
    val childStack = componentContext.createNavigation(DashboardScreen, NavigationType.CHILD)
    val navigation = componentContext.getNavigation(NavigationType.CHILD)

    val state = MutableStateFlow(
        MainViewState(
            MainViewState.MenuItem.values().filter(this::isFeatureAvailable)
        )
    )

    fun onSelectMenuItem(menuItem: MainViewState.MenuItem) {
        viewModelScope.launch {
            val newScreen = when (menuItem) {
                MainViewState.MenuItem.GENERAL -> DashboardScreen
                MainViewState.MenuItem.NAVIGATION -> NavigatorScreen
                MainViewState.MenuItem.APPS -> AppsScreen
                MainViewState.MenuItem.CHARGING -> ChargingScreen
            }
            navigation.replaceCurrent(newScreen)
        }
    }

    private fun isFeatureAvailable(menuItem: MainViewState.MenuItem): Boolean {
        return when (menuItem) {
            MainViewState.MenuItem.GENERAL,
            MainViewState.MenuItem.NAVIGATION,
            MainViewState.MenuItem.CHARGING -> true
            MainViewState.MenuItem.APPS -> appsFeatureAvailabilityInteractor.isFeatureAvailable
        }
    }
}