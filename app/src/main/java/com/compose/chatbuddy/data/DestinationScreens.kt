package com.compose.chatbuddy.data

sealed class DestinationScreens(var route : String){
   object userSignUp : DestinationScreens("signup")
   object userLogin : DestinationScreens("login")
   object userProfile : DestinationScreens("profile")
   object userChatList : DestinationScreens("chatList")
   object userSingleChat : DestinationScreens("singleChat/{chatId}"){
       fun createRoute(id : String) = "singleChat/$id"
   }
    object userStatusList : DestinationScreens("statusList")
    object userSingleStatus : DestinationScreens("singleStatus/{userId}"){
        fun createRoute(userId : String) = "singleStatus/$userId"
    }
}