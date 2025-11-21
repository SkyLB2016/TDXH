package com.sky.oa.test

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.PrintStream
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    // 强制使用UTF-8编码输出
    System.setOut(PrintStream(System.out, true, "UTF-8"))
    println("当前编码：" + System.getProperty("file.encoding"))
    println("控制台编码：" + System.getProperty("console.encoding"))
    println("launch 基本使用")
    val job = launch {
        delay(1000)
        println("launch 测试：协程执行完成")
    }
//    job.join()
//    job.cancel()
    val times= measureTimeMillis {
        val result1 = async {
            calculate(10)
        }
        val result2 = async { calculate(10) }
        println("计算结果：${result1.await()+result2.await()}")
    }
    println("并发执行时间：$times ms")
    val job2 = launch {
        launch {
            delay(1000)
            println("子任务1执行完成")
        }
        launch {
            delay(500)
            println("子任务2执行完成")
        }
    }
    // 示例4: 异常处理
    println("\n🚀 示例4: 异常处理")
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("捕获异常: $exception")
    }

    val scope = CoroutineScope(SupervisorJob() + exceptionHandler)
    scope.launch {
        println("抛出异常")
        throw RuntimeException("测试异常")
    }

    delay(1000)

}

suspend fun calculate(n:Int): Int {
    delay(1000)
    return n*n
}
