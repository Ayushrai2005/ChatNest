package ayush.chatnest

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import ayush.chatnest.Data.Event
import ayush.chatnest.Data.USER_NODE
import ayush.chatnest.Data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth : FirebaseAuth ,
    var db : FirebaseFirestore
): ViewModel() {



    var inProgress = mutableStateOf(false)
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

                        }
                    }
            }else{
                handleException(customMessage = "Number already Exists")
            }
        }

    }

    private fun createOrUpdateProfile(name: String?=null, phoneNumber: String?=null ,  userEmail: String?= null  , userPassword : String?=null  , imageUrl : String?=null) {
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

    fun login(userEmail: String , userPassword: String){


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
                    } else {
                        // If sign in fails, display a message to the user.
                        handleException(customMessage = "Login Failed")
                    }
                }

        }else{
            handleException(customMessage = "Please Fill in all Fields")
        }

    }
}

