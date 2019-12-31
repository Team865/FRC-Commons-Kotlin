package ca.warp7.frc.action

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream
import java.io.PrintStream

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
    fun testParallel() {
        executeUnrestricted(parallel {
            +runOnce { print("A") }
            +runOnce { print("B") }
        })
        assertEquals("AB", maskedOut.toString())
    }

    @Test
    fun testWait() {
        executeUnrestricted(parallel {
            +sequential {
                +wait(0.01)
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
    fun testDoubleSequential() {
        executeUnrestricted(parallel {
            +sequential {
                +wait(0.03)
                +runOnce {
                    print("1")
                }
            }
            +sequential {
                +wait(0.01)
                +runOnce {
                    print("2")
                }
            }
        })
        assertEquals("21", maskedOut.toString())
    }

    @Test
    fun testParallelInSequential() {
        executeUnrestricted(sequential {
            +parallel {
                +sequential {
                    +wait(0.01)
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