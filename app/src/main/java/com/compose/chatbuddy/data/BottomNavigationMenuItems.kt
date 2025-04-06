package com.compose.chatbuddy.data

import com.compose.chatbuddy.R

enum class BottomNavigationMenuItems(val icon: Int, val navDestination: DestinationScreens, val tabName : String) {
    CHATLIST(R.drawable.chat_menu_icon, DestinationScreens.userChatList,"Chat"),
    STATUSLIST(R.drawable.update_menu_icon, DestinationScreens.userStatusList, "Status"),
    PROFILE(R.drawable.profile_menu_icon, DestinationScreens.userProfile,"Profile")
}