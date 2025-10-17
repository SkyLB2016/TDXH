package com.sky.oa.test

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.Blocking

//val myScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//myScope.launch(Dispatchers.Main + CoroutineName("UI-Update")) {
//    try {
//        val data = fetchData() // 在 IO 线程执行
//        updateUI(data)         // 在 Main 线程执行
//    } catch (e: Exception) {
//        Log.e("MyTag", "Error in UI-Update", e)
//    }
//}

val handler = CoroutineExceptionHandler { _, exception ->
    println("Caught $exception")
}

val scope = CoroutineScope(SupervisorJob() + handler) // SupervisorJob 防止失败传染

// 定义一个 Flow
fun numbers(): Flow<Int> = flow {
    for (i in 10..13) {
        println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        delay(100)
        emit(i) // 发射值
    }
}

fun main() = runBlocking {
    println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
//    scope.launch {
//        throw RuntimeException("Failed in child 1")
//    }
//
//    scope.launch {
//        delay(100)
//        println("This will still execute") // 因为用了 SupervisorJob
//    }
//    val scope = MainScope() // SupervisorJob 防止失败传染
    val myScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

// 收集 Flow
    val nums=numbers()
        .map {
            it * it
        }          // 转换
        .filter { it % 2 == 1 }   // 过滤
        .onEach { println(it) }   // 副作用
        .launchIn(myScope)          // 在指定作用域启动收集
    println("nums:"+nums)
    val channel = Channel<String>()

    val sender = launch {
        repeat(5) {
            channel.send("Hello $it")
        }
        channel.close() // 关闭通道
    }

    val receiver = launch {
        for (msg in channel) { // 循环接收，直到通道关闭
            println("receive:" + msg)
        }
    }

    sender.join()
    receiver.join()
}
