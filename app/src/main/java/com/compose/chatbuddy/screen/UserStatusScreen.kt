package com.compose.chatbuddy.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun UserStatusScreen(navController: NavController, viewModel: ChatViewModel) {
    Text(text = "Status ", modifier = Modifier.padding(10.dp))
    var inProgressStatus = viewModel.inProgressStatus.value
    if (inProgressStatus) {
        commonProgressBar()
    } else {

        val statuses = viewModel.status.value
        val userData = viewModel.userData.value

        val myStatus = statuses.filter { it.chatUser.userId == userData?.userId }
        val otherStatus = statuses.filter { it.chatUser.userId == userData?.userId }

        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
            uri ->
            uri?.let {
                viewModel.uploadStatus(uri)
            }
        }

        Scaffold(
            floatingActionButton = {
                Fab {
                    launcher.launch("image/*")
                }
            },
            content = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(it)) {
                       titleText(title = "Status")
                    if(statuses.isEmpty()){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(it),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                          Text(text = "No Status Avaliable")
                        }
                    }else{
                         if(myStatus.isNotEmpty()){
                             commonRow(imageUrl = myStatus[0].chatUser.imageUrl ,
                                 name = myStatus[0].chatUser.name) {
                                 navigateTo(navController = navController, route =DestinationScreens.userSingleStatus.createRoute(myStatus[0].chatUser.userId!!))
                             }
                             commonDivider()
                             val uniqueUsers = otherStatus.map { it.chatUser }.toSet().toList()
                             LazyColumn(modifier = Modifier.weight(1f)){
                                 items(uniqueUsers){
                                     user ->
                                     commonRow(imageUrl = user.imageUrl, name = user.name) {
                                         navigateTo(navController = navController, route = DestinationScreens.userSingleStatus.createRoute(user.userId!!))
                                     }
                                 }
                             }
                         }

                    }

                    BottomNavMenu(
                        selectedItem = BottomNavigationMenuItems.STATUSLIST,
                        navController = navController
                    )
                }
            }
        )
    }

}

@Composable
fun Fab(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 70.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )
    }
}

