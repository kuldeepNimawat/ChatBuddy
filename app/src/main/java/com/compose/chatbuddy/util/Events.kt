package com.compose.chatbuddy.util

open class Events<out T>(val content : T) {
    var hasExceptionHandled = false
    fun getContentOrNull(): T?{
        return if(hasExceptionHandled) null
        else{
            hasExceptionHandled = true
            content
        }
    }
}