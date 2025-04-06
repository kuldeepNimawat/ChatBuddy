package com.compose.chatbuddy.model

data class Message(
    val sendBy : String? ="",
    val message : String? = "",
    val timeStamp : String? = ""
)
