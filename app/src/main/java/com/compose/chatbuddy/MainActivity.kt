package com.compose.chatbuddy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.compose.chatbuddy.data.DestinationScreens
import com.compose.chatbuddy.screen.SingleChatScreen
import com.compose.chatbuddy.screen.SingleStatusScreen
import com.compose.chatbuddy.screen.UserChatScreen
import com.compose.chatbuddy.screen.UserLoginScreen
import com.compose.chatbuddy.screen.UserProfileScreen
import com.compose.chatbuddy.screen.UserSignUpScreen
import com.compose.chatbuddy.screen.UserStatusScreen
import com.compose.chatbuddy.ui.theme.ChatBuddyTheme
import com.compose.chatbuddy.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    chatNavigationApp()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun chatNavigationApp() {
       val navController = rememberNavController()
    val viewModel = hiltViewModel<ChatViewModel>()
    NavHost(navController = navController, startDestination = DestinationScreens.userSignUp.route){
        composable(DestinationScreens.userSignUp.route){
            UserSignUpScreen(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreens.userLogin.route){
            UserLoginScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreens.userChatList.route){
            UserChatScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreens.userStatusList.route){
            UserStatusScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreens.userProfile.route){
            UserProfileScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreens.userSingleChat.route){
            val chatId = it.arguments?.getString("chatId")
            chatId?.let {
                SingleChatScreen(navController = navController, viewModel = viewModel,chatId = chatId)
            }
        }

        composable(DestinationScreens.userSingleStatus.route){
            val userId = it.arguments?.getString("userId")
            userId?.let {
                SingleStatusScreen(navController = navController, viewModel = viewModel,userId = userId)
            }
        }

    }
}
