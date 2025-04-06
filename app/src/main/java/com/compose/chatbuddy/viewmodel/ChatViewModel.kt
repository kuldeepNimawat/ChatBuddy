package com.compose.chatbuddy.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.compose.chatbuddy.data.MESSAGES
import com.compose.chatbuddy.data.STATUS
import com.compose.chatbuddy.data.USER_CHATS
import com.compose.chatbuddy.data.USER_NODE
import com.compose.chatbuddy.model.ChatData
import com.compose.chatbuddy.model.ChatUser
import com.compose.chatbuddy.model.Message
import com.compose.chatbuddy.model.Status
import com.compose.chatbuddy.model.UserData
import com.compose.chatbuddy.util.Events
import com.compose.chatbuddy.util.navigateTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    var isProgress = mutableStateOf(false)
    var isChatProgress = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Events<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    var chats = mutableStateOf<List<ChatData>>(listOf())
    var chatMessages = mutableStateOf<List<Message>>(listOf())
    var inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener : ListenerRegistration? = null

    var status = mutableStateOf<List<Status>>(listOf())
    var inProgressStatus = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun signUp(name: String, mobileNumber: String, email: String, password: String) {
        isProgress.value = true
        if(name.isEmpty() or mobileNumber.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMsg = "Please fill all fields")
            return
        }else{
           db.collection(USER_NODE).whereEqualTo("number", mobileNumber).get().addOnSuccessListener{
               if(it.isEmpty){
                   auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                       isProgress.value = false
                       if (it.isSuccessful) {
                           signIn.value = true
                           createOrUpdateProfile( name = name , mobileNumber = mobileNumber)
                           //Log.d("TAG", "SignUp : User Logged In")
                       } else {
                           handleException(it.exception, customMsg = "Sign up failed")
                       }
                   }
               }else{
                   handleException(customMsg = "Number Already Exists")
                   isProgress.value = false
               }
           }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun createOrUpdateProfile(name: String?=null, mobileNumber: String?=null, imageUrl : String?=null) {
         var uid =auth.currentUser?.uid
         val userData = UserData(
             userId = uid,
             name = name ?: userData.value?.name,
             number = mobileNumber ?: userData.value?.number,
             imageUrl = imageUrl ?: userData.value?.imageUrl
         )

        uid?.let {
            isProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener{
                if(it.exists()){
                    db.collection(USER_NODE).document(uid).update("name", userData?.name?:"" , "number",userData?.number)
                    isProgress.value = false
                    getUserData(uid)
                }else{
                    db.collection(USER_NODE).document(uid).set(userData)
                    isProgress.value = false
                    getUserData(uid)
                }
            }.addOnFailureListener{
                handleException(it, "Cannot retrieve user")
            }
        }
    }

    private fun getUserData(userId : String) {
          isProgress.value = true
        db.collection(USER_NODE).document(userId).addSnapshotListener{
            value, error ->
            if(error != null){
                handleException(error, "Cannot retrieve User")
            }
            if(value!=null){
                var user = value.toObject<UserData>()
                userData.value =user
                isProgress.value = false
                populateChats()
                populateStatus()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun uploadProfileImage(uri : Uri){
        uploadImage(uri){
               createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) ->Unit){
        isProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTast = imageRef.putFile(uri)
        uploadTast.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            isProgress.value = false
        }.addOnFailureListener{
            handleException(it)
        }
    }

    fun handleException(exception: Exception? = null, customMsg: String = "") {
        Log.e("ChatBuddyApp", "Chat Exception: " + exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMsg.isNullOrEmpty()) errorMsg else customMsg
        eventMutableState.value = Events(message)
        isProgress.value = false
    }

    fun loginIn(email : String, password: String){
        if(email.isEmpty() or password.isEmpty()){
            handleException(customMsg = "Please fill all fields")
        }else{
            isProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                if(it.isSuccessful){
                    signIn.value = true
                    isProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                }else{
                    handleException(it.exception, "Login Failed")
                }
            }
        }
    }

    fun logoutUser() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessage()
        currentChatMessageListener = null
        eventMutableState.value=Events("User Logout")
    }

    fun onAddChat(chatNumber: String) {
        if(chatNumber.isEmpty() or !chatNumber.isDigitsOnly()){
            handleException(customMsg = "Number must be contain digit only")
        }else{
            db.collection(USER_CHATS).where(Filter.or(Filter.and(
                Filter.equalTo("user1.number", chatNumber),
                Filter.equalTo("user2.number",userData.value?.number)
            ),
                Filter.equalTo("user1.number", userData.value?.number),
                Filter.equalTo("user2.number",chatNumber)
            )).get().addOnSuccessListener {
                if(it.isEmpty){
                    db.collection(USER_NODE).whereEqualTo("number",chatNumber).get().addOnSuccessListener {
                        if(it.isEmpty){
                            handleException(customMsg = "number not found")
                        }else{
                            val chatPartner = it.toObjects<UserData>()[0]
                            val id =db.collection(USER_CHATS).document().id
                            val chat = ChatData(
                                chatId = id,
                                ChatUser(userId = userData.value?.userId,
                                    name = userData.value?.name,
                                    imageUrl = userData.value?.imageUrl,
                                    number = userData.value?.number),
                                ChatUser(userId = chatPartner.userId,
                                    name = chatPartner.name,
                                    imageUrl = chatPartner.imageUrl,
                                    number = chatPartner.number)
                            )

                            db.collection(USER_CHATS).document(id).set(chat)
                        }
                    }
                        .addOnFailureListener{
                            handleException(it)
                        }
                }else{
                    handleException(customMsg = "chat already exists")
                }
            }
        }
    }

    fun populateChats(){
        isChatProgress.value = true
        db.collection(USER_CHATS).where(
            Filter.or(
                //Filter.equalTo(FieldPath.of("user1", "userId"), userData.value?.userId),
                //.equalTo(FieldPath.of("user2", "userId"), userData.value?.userId),
                Filter.equalTo("chatUser1.userId", userData.value?.userId),
                Filter.equalTo("chatUser2.userId", userData.value?.userId)
            )
        ).addSnapshotListener{
            value, error ->
            if(error!=null) {
                handleException(error)
            }

            if(value!=null){
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                isChatProgress.value = false
            }
        }
    }

    fun onSendReply(chatId : String, message : String){
           val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(USER_CHATS).document(chatId).collection(MESSAGES).document().set(msg)
    }

    fun populateMessages(chatId : String){
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(USER_CHATS).document(chatId).collection(MESSAGES)
            .addSnapshotListener{
                value, error ->
                if(error!=null){
                    handleException(error)
                }
                if(value!=null){
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.filter { it.timeStamp != null }
                        .sortedBy { it.timeStamp }
                    inProgressChatMessage.value = false
                }
            }
    }

    fun depopulateMessage(){
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun uploadStatus(uri: Uri) {
         uploadImage(uri){
            createStatus(uri.toString())
            //createStatus(null)
         }
    }

    fun createStatus(imageUrl : String?){
       val newStatus = Status(
           chatUser =  ChatUser(
               userId = userData.value?.userId,
               name = userData.value?.name,
               imageUrl = userData.value?.imageUrl,
               number = userData.value?.number
           ),
           imageUrl = imageUrl,
           timeStamp = System.currentTimeMillis()
       )

        db.collection(STATUS).document().set(newStatus)
    }

    fun populateStatus(){
        inProgressStatus.value = true
        val timeDelta = 24L * 24 * 60 * 60 * 1000
        val cutOffTime = System.currentTimeMillis() - timeDelta
        db.collection(USER_CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)

            )
        ).addSnapshotListener{
            value, error ->
            if(error!=null){
                handleException(error)
            }

            if(value!=null){
                val currentConnection = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach {
                    chat ->
                    if(chat.chatUser1.userId == userData.value?.userId){
                        currentConnection.add(chat.chatUser2.userId)
                    }else{
                        currentConnection.add(chat.chatUser1.userId)
                    }
                }

                db.collection(STATUS)/*.whereGreaterThan("timeStamp",cutOffTime)*/.whereIn("chatUser.userId", currentConnection).addSnapshotListener {
                        value, error ->
                    if(error!=null){
                        handleException(error)
                    }
                    if(value!=null){
                        status.value = value.toObjects()
                        inProgressStatus.value = false
                    }
                }
            }

        }
    }
}

