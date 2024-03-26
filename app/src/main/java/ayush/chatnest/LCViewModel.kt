package ayush.chatnest

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import ayush.chatnest.Data.CHAT_NODE
import ayush.chatnest.Data.ChatData
import ayush.chatnest.Data.ChatUser
import ayush.chatnest.Data.Event
import ayush.chatnest.Data.USER_NODE
import ayush.chatnest.Data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth : FirebaseAuth ,
    var db : FirebaseFirestore,
    val storage : FirebaseStorage
): ViewModel() {


    val chats = mutableStateOf<List<ChatData>>(listOf())
    var inProgress = mutableStateOf(false)
    var inProgressChat = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)

    var userData = mutableStateOf<User?>(null)
    init {
        auth.currentUser
        val currentUser = auth.currentUser
        signIn.value = currentUser!=null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signup(name : String , phoneNumber: String , userEmail : String , userPassword:String){
        inProgress.value = true

        db.collection(USER_NODE).whereEqualTo("phoneNumber" , phoneNumber).get() .addOnSuccessListener{

            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            // Inside your composable function
                            signIn.value = true
                            createOrUpdateProfile(name , phoneNumber ,userEmail , userPassword )


                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
//                    Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT)
//                        .show()
                        } else {
                            // If sign in fails, display a message to the user.
//                    Toast.makeText(context, "Registartion failed", Toast.LENGTH_SHORT)
//                        .show()
                            handleException(task.exception , customMessage = "SignUp Failed")
                            inProgress.value = false

                        }
                    }
            }else{
                handleException(customMessage = "Number already Exists")
            }
        }

    }

     fun createOrUpdateProfile(name: String?=null, phoneNumber: String?=null ,  userEmail: String?= null  , userPassword : String?=null  , imageUrl : String?=null) {
        var uid = auth.currentUser?.uid
        val userData = User(
            userId = uid ,
            name =  name?:userData.value?.name,
            phoneNumber = phoneNumber?: userData.value?.phoneNumber,
            imageUrl = imageUrl?:userData.value?.imageUrl,
            userEmail = userEmail?:userData.value?.userEmail ,
            userPassword = userPassword?:userData.value?.userPassword
        )

        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener{
                if(it.exists()){
                    //update user data
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid )
                }else{
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid )
                }
            }
                .addOnFailureListener{
                    handleException(it , "Cannot Retrieve User")
                }
        }
    }

    private fun getUserData(uid : String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener{
            value , error ->

            if(error!=null){
                handleException(error , "Cannot Retrieve User")
            }
            if(value != null){
                var user = value.toObject<User>()
                userData.value = user
                inProgress.value = false
            }
        }
    }

    fun handleException (exception: Exception?=null , customMessage : String = ""){

        Log.d("LiveChat" , "live chat exception " , exception)
        val errorMsg = exception?.localizedMessage?:""
        val message = if(customMessage.isNullOrBlank()) errorMsg else customMessage

        eventMutableState.value =  Event(message)



    }

    fun login(userEmail: String , userPassword: String , navController: NavController){


        if (userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
            inProgress.value = true
          auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        signIn.value = true
                        inProgress.value = false
                        auth.currentUser?.uid?.let {
                                getUserData(it)
                        }
                        navigateTo(navController = navController, DestinationScreen.ChatList.route)
                    } else {
                        // If sign in fails, display a message to the user.
                        handleException(customMessage = "Login Failed")
                    }
                }

        }else{
            handleException(customMessage = "Please Fill in all Fields")
        }

    }

    fun uploadProfileImage(uri: Uri) {
        UploadImage(uri){
            createOrUpdateProfile(imageUrl = it.toString())

        }

    }

    fun UploadImage(uri : Uri , onSuccess : (Uri) -> Unit){
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val email = auth.currentUser?.email
        val imageRef = storageRef.child("Images/${email}_${System.currentTimeMillis()}")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            inProgress.value = false
        }
            .addOnFailureListener{
                handleException(it)
            }


    }

    fun logout() {
        inProgress.value = true
        auth.signOut()
        inProgress.value = false
        userData.value= null
        eventMutableState.value = Event("Logged Out")
    }

fun onAddChat(number: String) {
    inProgressChat.value = true
    if(number.isEmpty() or !number.isDigitsOnly()){
        handleException(customMessage = "Please Enter a valid Number")
        inProgressChat.value = false
    }else{
        db.collection(CHAT_NODE).where(Filter.or(

            Filter.and(
                Filter.equalTo("user1.phoneNumber" , number),
                Filter.equalTo("user2.phoneNumber" , userData.value?.phoneNumber)
            ),
            Filter.and(
                Filter.equalTo("user2.phoneNumber" , number),
                Filter.equalTo("user1.phoneNumber" , userData.value?.phoneNumber)

            )

        )).get().addOnSuccessListener {
            if(it.isEmpty){
                db.collection(USER_NODE).whereEqualTo("phoneNumber" , number).get().addOnSuccessListener {
                    if(it.isEmpty){
                        handleException(customMessage = "User not found")
                        inProgressChat.value = false
                    }else{
                        val chatPartner = it.documents[0].toObject<User>()
                        if (chatPartner != null && userData.value != null) {
                            val id = db.collection(USER_NODE).document().id
                            val chat = ChatData(
                                chatId = id,
                                user1 = ChatUser(
                                    userId = userData.value?.userId,
                                    name = userData.value?.name,
                                    imageUrl = userData.value?.imageUrl,
                                    phoneNumber = userData.value?.phoneNumber
                                ),
                                user2 = ChatUser(
                                    userId = chatPartner.userId,
                                    name = chatPartner.name,
                                    imageUrl = chatPartner.imageUrl,
                                    phoneNumber = chatPartner.phoneNumber
                                )
                            )
                            db.collection(CHAT_NODE).document(id).set(chat).addOnSuccessListener {
                                inProgressChat.value = false
                            }
                                .addOnFailureListener{
                                    handleException(it)
                                    inProgress.value = false
                                }
                        } else {
                            handleException(customMessage = "User data is null")
                        }
                    }
                }
                    .addOnFailureListener{
                        inProgress.value = false
                        handleException(it)
                    }
            }
        }.addOnFailureListener {
            inProgressChat.value = false
            handleException(it)
        }
    }
}


}

