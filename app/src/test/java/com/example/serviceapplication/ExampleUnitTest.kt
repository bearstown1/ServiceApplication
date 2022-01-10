package com.example.serviceapplication

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.junit.Test

import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
/*
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
*/

    @Test
    fun main() {
        GlobalScope.launch {
            println("start")
            // val async1 = async { "async1"}
            // val async2 = async { "async2"}
            // println(async1.await())

            /*withContext(Dispatchers.IO){
                testSuspend1()
                testSuspend2()
            }*/

   /*         val time  = measureTimeMillis {
                testSuspend1()
                testSuspend2()
            }

            println("측정시간 : $time")*/

            launch {
                delay(500)
                println("launch1")
            }

            launch {
                delay(300)
                println("launch2")
            }

            println("end")

        }


        Thread.sleep(5000)
    }

    @Test
    fun testFun2 () {
        GlobalScope.launch {
            println("start")

            launch {
                println("launch2")
            }

            launch {
                println("launch1")
            }

            println("end")
        }


        Thread.sleep(6000)
    }

    @Test
    fun testFun3 () {
        GlobalScope.launch {
            println("start")

            async { testSuspend1() }.await()

            launch {
                delay(100)
                println("launch1")
            }


            withContext(Dispatchers.IO) {
                delay(200)
                println("withContext1")
            }

            println("end")

        }

        Thread.sleep(5000)
    }

    @Test
    fun measureTime () {
        GlobalScope.launch {
            val time = measureTimeMillis {
                val time1 = async { testSuspend1() }
                val time2 = async { testSuspend2() }

                time1.await()
                time2.await()
            }

            println("걸린시간 : $time")
        }

        Thread.sleep(6000)
    }



    suspend fun testSuspend1() {
        delay(500)
        println("testSuspend1")
    }

    suspend fun testSuspend2() {
        delay(300)
        println("testSuspend2")
    }


    @Test
    fun flowTest() = runBlocking{
        println("begin")

        launch {
            for (k in 1..3){
                println("Im blocked $k")
            }
        }

        val foo: Flow<Int> = flow {
            for (i in 1..3) {
                emit(i)
            }
        }

        foo.collect { value ->
            println(value)
        }

        println("end")

        delay(5000)
    }

    @Test
    fun hotFlowTest() = runBlocking {
        val stringFlow: Flow<String> = flow {
            for (i in 0..10) {
                println("integer: $i")
                emit("integer: $i")
                delay(100)
            }
        }

        val stateFlow = stringFlow.stateIn(
            scope = CoroutineScope(Dispatchers.IO)
        )

        delay(200)

        stateFlow.collect() {
            println("hot : ${it}")
        }
    }

}

