package ru.starfactory.core.bluetooth.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import ru.starfactory.core.bluetooth.domain.BluetoothDevice
import android.bluetooth.BluetoothDevice as AndroidBluetoothDevice


class AndroidBluetoothService(context: Context) : BluetoothService {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    override fun getIsEnabled(): Boolean = bluetoothAdapter.isEnabled

    @SuppressLint("MissingPermission")
    override fun getBoundedDevices(): List<BluetoothDevice> = bluetoothAdapter.bondedDevices.toBluetoothDevices()

    override fun observeBoundedDevices(): Flow<List<BluetoothDevice>> {
        return flow { emit(getBoundedDevices()) } // TODO Sumin
    }

//    @SuppressLint("MissingPermission")
//    override suspend fun connect(
//        address: String,
//        channelId: UUID,
//        block: suspend CoroutineScope.(BluetoothService.BluetoothConnection) -> Unit
//    ) = withContext(Dispatchers.IO) {
//        val device = bluetoothAdapter.getRemoteDevice(address)
//        println(device.address)
//        device.createRfcommSocketToServiceRecord(channelId).use { channel ->
//            channel.connect()
//
//            val connection = object : BluetoothService.BluetoothConnection {
//                override val inputStream: InputStream = channel.inputStream
//                override val outputStream: OutputStream = channel.outputStream
//            }
//            coroutineScope {
//                block(this, connection)
//            }
//        }
//    }

    companion object {
        // private const val TAG = Tag.BLUETOOTH
    }
}

private fun Set<AndroidBluetoothDevice>.toBluetoothDevices() = map { it.toBluetoothDevice() }

@SuppressLint("MissingPermission")
private fun AndroidBluetoothDevice.toBluetoothDevice() = BluetoothDevice(
    address = this.address,
    name = this.name,
)