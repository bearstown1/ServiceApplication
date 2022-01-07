package com.example.serviceapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.serviceapplication", appContext.packageName)
    }

    @Test
    fun closingTest() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm Sleeping $i...")
                    delay(500L)
                }
            } finally {
                println("job : running finally")
            }
        }

        delay(1300L)
        println("main: i'm tired of waiting")
        job.cancelAndJoin()
        println("main: Now i can quit")
    }

    @Test
    fun main() {
        println("begin")

        GlobalScope.launch {
            println("launch")
        }

        println("end")

    }


}

