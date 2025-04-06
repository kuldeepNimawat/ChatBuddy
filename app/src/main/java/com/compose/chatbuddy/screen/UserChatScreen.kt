package com.compose.chatbuddy.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.chatbuddy.data.BottomNavigationMenuItems
import com.compose.chatbuddy.data.DestinationScreens
import com.compose.chatbuddy.util.commonDivider
import com.compose.chatbuddy.util.commonProgressBar
import com.compose.chatbuddy.util.commonRow
import com.compose.chatbuddy.util.navigateTo
import com.compose.chatbuddy.util.titleText
import com.compose.chatbuddy.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatScreen(navController: NavController, viewModel: ChatViewModel) {
    val inChatProgress = viewModel.isChatProgress

    if (inChatProgress.value) {
        commonProgressBar()
    } else {
        val chats = viewModel.chats.value
        val userData = viewModel.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = {
            showDialog.value = true
        }
        val onDismiss: () -> Unit = {
            showDialog.value = false
        }
        val onAddChatUser: (String) -> Unit = {
            viewModel.onAddChat(it)
            showDialog.value = false
        }

        Scaffold(
            floatingActionButton = {
                fab(
                    showDialog = showDialog.value,
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    onAddChatUser = onAddChatUser
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    titleText(title = "Chats")

                    commonDivider()

                    if (chats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Chats Available")
                        }
                    }else{
                        LazyColumn(modifier = Modifier.padding(10.dp)){
                            items(chats){
                                chat ->
                                val chatUser = if(chat.chatUser1.userId == userData?.userId){
                                    chat.chatUser2
                                }else{
                                    chat.chatUser1
                                }

                                commonRow(imageUrl = chatUser.imageUrl, name = chatUser.name) {
                                    chat.chatId?.let {
                                        navigateTo(navController, route = DestinationScreens.userSingleChat.createRoute(id = it))
                                    }
                                }

                                commonDivider()
                            }
                        }
                    }

                    BottomNavMenu(
                        selectedItem = BottomNavigationMenuItems.CHATLIST,
                        navController = navController
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fab(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChatUser: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        }, confirmButton = {
            Button(onClick = {
                onAddChatUser(addChatNumber.value)
            }) {
                Text(text = "Add Chat User")
            }
        },
            title = { Text(text = "Add Chat User") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value, onValueChange = { addChatNumber.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            })
    }

    FloatingActionButton(
        onClick = {
            onFabClick()
        }, containerColor = Color.Gray,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 80.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = "", tint = Color.White)
    }
}