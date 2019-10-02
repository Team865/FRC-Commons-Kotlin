package test.ca.warp7.frc.action

import ca.warp7.frc.action.async
import ca.warp7.frc.action.queue
import ca.warp7.frc.action.runOnce
import ca.warp7.frc.action.wait
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class AsyncTest {

    companion object {

        private val maskedOut = ByteArrayOutputStream()
        private val systemOut = System.out

        @BeforeAll
        @JvmStatic
        fun setUpStreams() {
            System.setOut(PrintStream(maskedOut))
        }

        @AfterAll
        @JvmStatic
        fun restoreStreams() {
            System.setOut(systemOut)
        }
    }


//    @Test
//    fun async12() {
//        executeUnrestricted(async {
//            +runOnce { print("1") }
//            +runOnce { print("2") }
//        })
//        assertEquals("12", maskedOut.toString())
//    }

    @Test
    fun async12wait() {
        executeUnrestricted(async {
            +queue {
                +wait(0.1)
                +runOnce {
                    print("1")
                }
            }
            +runOnce { print("2") }
        })
        assertEquals("21", maskedOut.toString())
    }
//
//    @Test
//    fun async2queue() {
//        executeUnrestricted(async {
//            +queue {
//                +wait(0.1)
//                +runOnce {
//                    print("1")
//                }
//            }
//            +queue {
//                +wait(0.06)
//                +runOnce {
//                    print("2")
//                }
//            }
//        })
//        assertEquals("21", maskedOut.toString())
//    }
//
//    @Test
//    fun asyncInQueue() {
//        executeUnrestricted(queue {
//            +async {
//                +queue {
//                    +wait(0.1)
//                    +runOnce {
//                        print("1")
//                    }
//                }
//                +runOnce { print("2") }
//            }
//            +runOnce { print("3") }
//        })
//        assertEquals("213", maskedOut.toString())
//    }
}