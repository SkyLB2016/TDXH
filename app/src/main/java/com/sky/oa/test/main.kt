package com.sky.oa.test

import kotlinx.coroutines.*

fun main() = runBlocking { // runBlocking 也是一个构建器，通常用于测试或主函数
    // 使用 launch 启动协程
    val job = launch {
        println("launch start")
        delay(4100)
        println("launch end")
    }

    // 使用 async 启动协程
    val deferred = async {
        println("async start")
        delay(5100)
        "async result"
    }

    // 等待结果
    println("async result: ${deferred.await()}")

    // 等待 launch 协程完成
    job.join()
    
}
