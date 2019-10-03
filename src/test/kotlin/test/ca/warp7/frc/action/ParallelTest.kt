package test.ca.warp7.frc.action

import ca.warp7.frc.action.*
import org.junit.jupiter.api.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class ParallelTest {

    private val maskedOut = ByteArrayOutputStream()
    private val systemOut = System.out

    @BeforeEach
    fun setUpStreams() {
        System.setOut(PrintStream(maskedOut))
    }

    @AfterEach
    fun restoreStreams() {
        System.setOut(systemOut)
    }

    @Test
    fun async12() {
        executeUnrestricted(parallel {
            +runOnce { print("A") }
            +runOnce { print("B") }
        })
        assertEquals("AB", maskedOut.toString())
    }

    @Test
    fun async12wait() {
        executeUnrestricted(parallel {
            +sequential {
                +wait(0.1)
                +runOnce {
                    print("1")
                }
            }
            +runOnce { print("2") }
        })
        assertEquals("21", maskedOut.toString())
    }
}