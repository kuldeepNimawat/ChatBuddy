package com.compose.chatbuddy.model

data class ChatData(
    val chatId : String? = "",
    val chatUser1 : ChatUser = ChatUser(),
    val chatUser2 : ChatUser = ChatUser()
)

data class ChatUser(
    val userId : String? = "",
    val imageUrl: String? ="",
    val number: String? ="",
    val name: String? =""
)
