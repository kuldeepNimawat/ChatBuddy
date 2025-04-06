package com.compose.chatbuddy.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.chatbuddy.R
import com.compose.chatbuddy.data.DestinationScreens
import com.compose.chatbuddy.util.checkSignedIn
import com.compose.chatbuddy.util.commonProgressBar
import com.compose.chatbuddy.util.navigateTo
import com.compose.chatbuddy.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLoginScreen(navController: NavController, viewModel: ChatViewModel){

    checkSignedIn(viewModel = viewModel, navController = navController)

    val userEmailState = remember {
        mutableStateOf(TextFieldValue())
    }

    val userPasswordState = remember {
        mutableStateOf(TextFieldValue())
    }

    val focus = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.chat_icon), contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .verticalScroll(rememberScrollState())
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sign In", modifier = Modifier
                    .padding(10.dp),
                style = TextStyle(
                    fontSize = 20.sp, fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
            )

            OutlinedTextField(value = userEmailState.value, onValueChange = {
                userEmailState.value = it
            }, modifier = Modifier.padding(8.dp),
                label = { Text(text = "Email") })

            OutlinedTextField(value = userPasswordState.value, onValueChange = {
                userPasswordState.value = it
            }, modifier = Modifier.padding(8.dp),
                label = { Text(text = "Password") })

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                    viewModel.loginIn(userEmailState.value.text,userPasswordState.value.text)
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = "SIGN IN", style = TextStyle(fontSize = 20.sp))
            }

            Text(
                text = "New User? Go to Sign Up ->", modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreens.userSignUp.route)
                    },
                color = Color.Blue
            )

        }

        if(viewModel.isProgress.value){
            commonProgressBar()
        }

    }
}