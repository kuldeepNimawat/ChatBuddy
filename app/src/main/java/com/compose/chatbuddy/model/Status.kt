package com.compose.chatbuddy.model

data class Status(
    val chatUser: ChatUser = ChatUser(),
    var imageUrl : String? = "",
    var timeStamp : Long? = null
)
