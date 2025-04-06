package com.compose.chatbuddy.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.chatbuddy.model.Message
import com.compose.chatbuddy.ui.theme.lightGreen
import com.compose.chatbuddy.ui.theme.lightGrey
import com.compose.chatbuddy.util.commonDivider
import com.compose.chatbuddy.util.commonImage
import com.compose.chatbuddy.viewmodel.ChatViewModel

@Composable
fun SingleChatScreen(navController: NavController, viewModel: ChatViewModel, chatId: String) {
     //Text(text = chatId, color = Color.Black, fontSize = 24.sp)
     var reply by rememberSaveable {
          mutableStateOf("")
     }

    val onSendReply  ={
        viewModel.onSendReply(chatId = chatId,reply)
        reply = ""
    }

    val myUser = viewModel.userData.value
    val currentChat = viewModel.chats.value.first{it.chatId == chatId}
    val chatUser = if(myUser?.userId == currentChat.chatUser1.userId){
        currentChat.chatUser2
    }else{
        currentChat.chatUser1
    }

    val chatMessages = viewModel.chatMessages

    LaunchedEffect(key1 = Unit){
     viewModel.populateMessages(chatId)
    }

    BackHandler {
          viewModel.depopulateMessage()
    }

    Column {
        chatHeader(name = chatUser.name?: "", imageUrl = chatUser.imageUrl?:"") {
            navController.popBackStack()
            viewModel.depopulateMessage()
        }
        messageBox(modifier = Modifier.weight(1f), chatMessages = chatMessages.value, currentUserId = myUser?.userId?:"")
        replyBox(reply, onReplyChange = {reply = it}, onSendReply)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun replyBox(reply : String, onReplyChange : (String) -> Unit, onSendReply : () ->Unit){
     Column(modifier = Modifier.fillMaxWidth()) {
          commonDivider()
          Row(modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
               horizontalArrangement = Arrangement.SpaceBetween) {
              OutlinedTextField(value = reply, onValueChange = onReplyChange, maxLines = 3)
               Button(onClick = onSendReply
               ) {
                    Text(text = "Send")
               }
          }
     }
}

@Composable
fun chatHeader(name : String, imageUrl : String, onBackClicked: () -> Unit){
     Row(modifier = Modifier
         .fillMaxWidth()
         .wrapContentHeight(),
         verticalAlignment = Alignment.CenterVertically) {
         Icon(Icons.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
             .clickable {
                 onBackClicked.invoke()
             }
             .padding(10.dp))

         commonImage(data = imageUrl, modifier = Modifier
             .padding(10.dp)
             .size(50.dp)
             .clip(CircleShape))

         Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 5.dp))
     }
}

@Composable
fun messageBox(
    modifier: Modifier,
    chatMessages : List<Message>, currentUserId : String
){
    LazyColumn(modifier = modifier, reverseLayout = true){
        items(chatMessages){
            msg ->
            val alignment = if(msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if(msg.sendBy == currentUserId) lightGreen else lightGrey
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), horizontalAlignment = alignment) {
                Text(text = msg.message?:"", modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}