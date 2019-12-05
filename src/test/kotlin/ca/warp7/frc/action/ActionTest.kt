package ca.warp7.frc.action

import org.junit.jupiter.api.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class ActionTest {

    private fun executeUnrestricted(action: Action) {
        action.firstCycle()
        while (!action.shouldFinish()) {
            action.update()
            Thread.sleep(20)
        }
        action.lastCycle()
    }

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
    fun p12() {
        executeUnrestricted(parallel {
            +runOnce { print("A") }
            +runOnce { print("B") }
        })
        assertEquals("AB", maskedOut.toString())
    }

    @Test
    fun p12wait() {
        executeUnrestricted(parallel {
            +sequential {
                +wait(0.1)
                +runOnce {
                    print("1")
                }
                +runOnce {
                    print("2")
                }
            }
            +runOnce { print("3") }
        })
        assertEquals("312", maskedOut.toString())
    }


    @Test
    fun p2s() {
        executeUnrestricted(parallel {
            +sequential {
                +wait(0.1)
                +runOnce {
                    print("1")
                }
            }
            +sequential {
                +wait(0.06)
                +runOnce {
                    print("2")
                }
            }
        })
        assertEquals("21", maskedOut.toString())
    }

    @Test
    fun pInS() {
        executeUnrestricted(sequential {
            +parallel {
                +sequential {
                    +wait(0.1)
                    +runOnce {
                        print("1")
                    }
                }
                +runOnce { print("2") }
            }
            +runOnce { print("3") }
        })
        assertEquals("213", maskedOut.toString())
    }
}