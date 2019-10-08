package ca.warp7.frc.log

interface CSVLogger : AutoCloseable {
    fun withHeaders(vararg headers: String): CSVLogger
    fun writeData(vararg data: Number)
}