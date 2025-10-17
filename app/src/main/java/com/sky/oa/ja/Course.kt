package com.sky.oa.ja

import java.io.Serializable

/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2021/8/16 7:01 下午
 * @Version: 1.0
 */
data class Course(var name: String, var score: Float) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
        const val CAA=1
    }
}