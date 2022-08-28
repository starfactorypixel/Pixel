package ru.starfactory.core.serial.usb.domian

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.starfactory.core.serial.domain.SerialDevice
import ru.starfactory.core.serial.domain.SerialDeviceId
import ru.starfactory.core.serial.domain.SerialDeviceType

interface UsbSerialInteractor {
    fun observeUsbSerialDevices(): Flow<List<SerialDevice>>
}

internal class UsbSerialInteractorImpl : UsbSerialInteractor {
    override fun observeUsbSerialDevices(): Flow<List<SerialDevice>> {
        val devices = buildList<SerialDevice> {
            add(FakeUsbDevice(id = SerialDeviceId(type = SerialDeviceType.USB, "/dev/usb0"), name = "Arduino Uno", type = SerialDeviceType.USB))
            add(FakeUsbDevice(id = SerialDeviceId(type = SerialDeviceType.USB, "/dev/usb1"), name = "esp32", type = SerialDeviceType.USB))
            add(FakeUsbDevice(id = SerialDeviceId(type = SerialDeviceType.USB, "/dev/usb2"), name = "stm32", type = SerialDeviceType.USB))
        }
        return flowOf(devices)
    }

    private data class FakeUsbDevice(
        override val id: SerialDeviceId,
        override val type: SerialDeviceType,
        override val name: String,
    ) : SerialDevice
}