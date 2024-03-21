package ayush.chatnest

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import ayush.chatnest.Data.Event
import ayush.chatnest.Screens.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth : FirebaseAuth
): ViewModel() {
    init {

    }
    var inProgress = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    fun signup(name : String , phoneNumber: String , userEmail : String , userPassword:String){

        inProgress.value = true
        fun addUserToDatabase(user: User){
            Firebase.firestore.collection("Users").document(userEmail)
                .set(user)
                .addOnSuccessListener {
//                    Toast.makeText(context, "User Saved ", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                }
        }
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    // Inside your composable function
                    addUserToDatabase(
                        User(
                            userEmail,
                            phoneNumber,
                            userPassword,
                            name
                        )
                    )

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
    }

    fun handleException (exception: Exception?=null , customMessage : String = ""){

        Log.d("LiveChat" , "live chat exception " , exception)
        val errorMsg = exception?.localizedMessage?:""
        val message = if(customMessage.isNullOrBlank()) errorMsg else customMessage


        eventMutableState.value =  Event(message)

    }
}

