package ru.starfactory.core.serial

import org.kodein.di.DI
import org.kodein.di.bindSet
import org.kodein.di.bindSingleton
import ru.starfactory.core.di.Modules
import ru.starfactory.core.di.i
import ru.starfactory.core.serial.domain.SerialDevicesProvider
import ru.starfactory.core.serial.domain.SerialInteractor
import ru.starfactory.core.serial.domain.SerialInteractorImpl

fun Modules.coreSerial() = DI.Module("core-serial") {
    bindSet<Pair<String, SerialDevicesProvider>>()
    bindSingleton<SerialInteractor> { SerialInteractorImpl(i(), i()) }
}
