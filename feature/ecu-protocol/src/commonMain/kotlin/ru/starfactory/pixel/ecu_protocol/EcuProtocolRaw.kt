package ru.starfactory.pixel.ecu_protocol

import java.io.InputStream
import java.io.OutputStream

// TODO Sumin: это тестовая не оптимальная реализация протокола.
// В будущем, после того как закрепим это поведение тестами, перепишем это оптимально
@Suppress("MagicNumber")
class EcuProtocolRaw(
    inputStream: InputStream,
    outputStream: OutputStream,
) {
    private val i = InputStreamReader(inputStream)
    private val o = OutputStreamWriter(outputStream)

    fun readMessage(): EcuMessage {
        check(i.readByte() == START_BYTE) { "Unexpected start byte" }
        i.resetCrc()

        val cfg1 = i.readByte()
        val cfg2 = i.readByte()

        val id = i.readShortAsInt()

        val len = i.readByteAsInt()
        check(len <= 64)

        val data = ByteArray(len) { i.readByte() }

        val crc = i.crc

        val packetCrc = i.readShortAsInt()
        check(crc == packetCrc.toShort()) { "Incorrect crc" }

        val protocolVersion = cfg1.toInt() ushr 5
        check(protocolVersion == PROTOCOL_VERSION) { "Unknown protocol version $protocolVersion" }

        check(i.readByte() == STOP_BYTE) { "Unexpected stop byte" }
        check(cfg2.toInt() ushr 7 and 0x1 == 1) { "Incorrect direction flag" }

        return EcuMessage(
            type = EcuMessage.Type.fromRaw(cfg2.toInt() and 0x1f),
            id = id,
            data = data
        )
    }

    fun writeMessage(message: EcuMessage) {
        o.writeByte(START_BYTE)
        o.resetCrc()
        o.writeByte((PROTOCOL_VERSION shl 5).toByte())
        o.writeByte(message.type.raw.toByte())
        o.writeShort(message.id.toShort())
        check(message.data.size <= MAX_DATA_LENGTH) { "Data size ${message.data.size} more that max data size" }
        o.writeByte(message.data.size.toByte())
        message.data.forEach { o.writeByte(it) }
        o.writeShort(o.crc)
        o.writeByte(STOP_BYTE)
    }

    private class InputStreamReader(private val inputStream: InputStream) : CrcCalculator() {
        fun readByte(): Byte {
            val data = inputStream.read().toByte()
            updateCrc(data)
            return data
        }

        fun readByteAsInt() = readByte().toInt() and 0xFF

        fun readShortAsInt(): Int {
            return (readByteAsInt() shl 8) or readByteAsInt()
        }
    }

    private class OutputStreamWriter(private val outputStream: OutputStream) : CrcCalculator() {
        fun writeByte(data: Byte) {
            updateCrc(data)
            outputStream.write(data.toInt())
        }

        fun writeShort(data: Short) {
            writeByte((data.toInt() ushr 8 and 0xFF).toByte())
            writeByte((data.toInt() and 0xFF).toByte())
        }
    }

    private open class CrcCalculator {
        var crc: Short = 0xFFFF.toShort()
            private set

        protected fun updateCrc(data: Byte) {
            crc = CRC16MCRF4XX.update(crc, data)
        }

        fun resetCrc() {
            crc = 0xFFFF.toShort()
        }
    }

    companion object {
        private val TYPE_MAPPING = EcuMessage.Type.values().associateBy { it.raw }

        private fun EcuMessage.Type.Companion.fromRaw(raw: Int): EcuMessage.Type {
            return TYPE_MAPPING[raw] ?: error("Unknown type $raw")
        }

        private const val PROTOCOL_VERSION = 0x0

        private const val MAX_DATA_LENGTH = 64

        private const val START_BYTE: Byte = 0x3C
        private const val STOP_BYTE: Byte = 0x3E
    }
}
