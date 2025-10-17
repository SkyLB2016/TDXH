package com.sky.oa.data.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sky.oa.data.model.User
import kotlinx.coroutines.delay

/**
 * 用户数据仓库类
 * 负责数据的获取和存储逻辑，模拟网络请求
 */
class UserRepository {
    // 模拟数据库中的用户数据
    private val mockUsers = listOf(
        User(1, "Alice Johnson", "alice@example.com"),
        User(2, "Bob Smith", "bob@example.com"),
        User(3, "Charlie Brown", "charlie@example.com")
    )

//    private val _users = MutableLiveData<List<User>>()
//    val users: LiveData<List<User>> = _users

//    init {
//        // 模拟加载数据
//        loadUsers()
//    }
//
//    private fun loadUsers() {
//        val mockUsers = listOf(
//            User(1, "Alice Johnson", "alice@example.com"),
//            User(2, "Bob Smith", "bob@example.com"),
//            User(3, "Charlie Brown", "charlie@example.com")
//        )
//        _users.value = mockUsers
//    }
    /**
     * 获取所有用户列表
     */
    suspend fun getUsers(): List<User> {
        delay(1000) // 模拟网络延迟
        return mockUsers
    }

    /**
     * 根据 ID 获取单个用户
     */
    suspend fun getUserById(id: Int): User? {
        delay(1000)
        return mockUsers.find { it.id == id }
    }

    /**
     * 添加新用户
     */
    suspend fun addUser(user: User): Boolean {
        // 模拟添加操作
        delay(500)
        return true
    }
}