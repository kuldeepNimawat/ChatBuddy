package com.compose.chatbuddy.util

import android.widget.AdapterView.OnItemClickListener
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.compose.chatbuddy.R
import com.compose.chatbuddy.data.DestinationScreens
import com.compose.chatbuddy.viewmodel.ChatViewModel

fun navigateTo(navController: NavController, route : String){
    navController.navigate(route){
        popUpTo(route)
        launchSingleTop=true
    }
}

@Composable
fun commonProgressBar(){
    Row(modifier = Modifier
        .alpha(0.5f)
        .background(Color.LightGray)
        .clickable(enabled = false) {}
        .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        CircularProgressIndicator()
    }
}

@Composable
fun checkSignedIn(viewModel: ChatViewModel, navController: NavController){
     val alreadySingedIn = remember {
         mutableStateOf(false)
     }

    val signIn = viewModel.signIn.value
    if(signIn && !alreadySingedIn.value){
        alreadySingedIn.value = true
        navController.navigate(DestinationScreens.userChatList.route){
            popUpTo(0)
        }
    }
}

@Composable
fun commonDivider(){
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun commonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = if (data.isNullOrEmpty()) R.drawable.profile_menu_item else data, // Use placeholder if data is null
        contentDescription = "Sample Image",
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun titleText(title : String){
    Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
}

@Composable
fun commonRow(imageUrl : String?, name : String?, onItemClick : () -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .clickable {
            onItemClick.invoke()
        }, verticalAlignment = Alignment.CenterVertically){
        commonImage(data = imageUrl, modifier = Modifier
            .padding(10.dp)
            .size(60.dp)
            .clip(shape = CircleShape)
            .background(Color.Red))

        Text(text = name ?: "...", fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp))
    }
}