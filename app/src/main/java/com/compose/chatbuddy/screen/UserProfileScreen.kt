package com.compose.chatbuddy.screen

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.chatbuddy.data.BottomNavigationMenuItems
import com.compose.chatbuddy.data.DestinationScreens
import com.compose.chatbuddy.util.commonDivider
import com.compose.chatbuddy.util.commonImage
import com.compose.chatbuddy.util.commonProgressBar
import com.compose.chatbuddy.util.navigateTo
import com.compose.chatbuddy.viewmodel.ChatViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun UserProfileScreen(navController: NavController, viewModel: ChatViewModel) {
    val isProgress = viewModel.isProgress.value
    val context = LocalContext.current
    if (isProgress) {
        commonProgressBar()
    } else {
        val userData = viewModel.userData.value
        var name by rememberSaveable {
            mutableStateOf(userData?.name?:"")
        }

        var number by rememberSaveable {
            mutableStateOf(userData?.number?:"")
        }
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                profileContent(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    viewModel = viewModel, name = name, onNameChange = {  name = it},
                    number = number, onNumberChange = { number = it},
                    onBack = {
                             navigateTo(navController = navController, DestinationScreens.userChatList.route)
                    },
                    onSave = {
                        Toast.makeText(context, "$name , $number", Toast.LENGTH_SHORT).show()
                        viewModel.createOrUpdateProfile(name = name, mobileNumber = number)
                    },
                    onLogout = {
                        viewModel.logoutUser()
                        navigateTo(navController = navController,DestinationScreens.userLogin.route)
                    }
                )

                BottomNavMenu(
                    selectedItem = BottomNavigationMenuItems.PROFILE,
                    navController = navController
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun profileContent(
    modifier: Modifier,
    viewModel: ChatViewModel,
    name: String,
    onNameChange: (String) -> Unit,
    number: String,
    onNumberChange: (String) -> Unit
    , onBack: () -> Unit, onSave: () -> Unit,
    onLogout: () -> Unit,
) {
    val userData = viewModel.userData
    //val
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save", modifier = Modifier.clickable {
                onSave.invoke()
            })
        }

        commonDivider()
        val imageUrl = viewModel.userData.value?.imageUrl
        profileImage(imageUrl = imageUrl, viewModel = viewModel)
        commonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name ", modifier = Modifier.width(100.dp))
            TextField(value = name, onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    containerColor = Color.Transparent)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Number ", modifier = Modifier.width(100.dp))
            TextField(value = number, onValueChange = onNumberChange,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    containerColor = Color.Transparent)
            )
        }
        commonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Logout", modifier = Modifier.clickable { onLogout.invoke() })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun profileImage(imageUrl: String?, viewModel: ChatViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadProfileImage(uri)
        }
    }

    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/* ")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                commonImage(data = imageUrl)
            }

            Text(text = "Change Profile Image")
        }

        if (viewModel.isProgress.value) {
            commonProgressBar()
        }
    }
}