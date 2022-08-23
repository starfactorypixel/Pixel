package ru.starfactory.pixel.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.starfactory.pixel.ui.screen.root.RootComponent
import ru.starfactory.pixel.ui.screen.root.RootView

class MainActivity : AbstractActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultComponentContext = defaultComponentContext()
        val rootComponent = defaultComponentContext.instanceKeeper.getOrCreate { RootComponent(di, defaultComponentContext) }

        setContent {
            RootView(rootComponent)
        }
    }
}
