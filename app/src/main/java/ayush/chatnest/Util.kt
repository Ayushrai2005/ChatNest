package ayush.chatnest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

fun navigateTo(navController: NavController , route : String){

    navController.navigate(route){
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun commonProgressBar(){

    Row(
        modifier = Modifier.alpha(0.5f).background(Color.LightGray).clickable(enabled = false){}
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()

    }
}

@Composable
fun CheckSignedIn(vm : LCViewModel , navController: NavController){
    val alreadySignIn  =  remember{ mutableStateOf(false) }
    val signIn = vm.signIn.value

    if(signIn && !alreadySignIn.value){
        alreadySignIn.value = true
        navController.navigate(DestinationScreen.ChatList.route){
            popUpTo(0)
        }
    }

}

//fun signOut(){
//
//    //Makes user singout by using auth
//    FirebaseAuth.getInstance().signOut()
//
//    navController.navigate("login_page") {
//        // Pop up to the login page so the back button doesn't go back to the logged-in state
//        popUpTo(navController.graph.startDestinationId)
//        // SingleTop ensures that if the login page is already on top, it won't create a new instance
//        launchSingleTop = true
//    }
//}