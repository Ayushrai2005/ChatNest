package ayush.chatnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ayush.chatnest.Screens.ChatListScreen
import ayush.chatnest.Screens.LoginPage
import ayush.chatnest.Screens.SignUpScreen
import ayush.chatnest.ui.theme.ChatNestTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(val route : String){
    object  SignUp : DestinationScreen ("signup")
    object  Login : DestinationScreen ("login")
    object  Profile : DestinationScreen ("profile")
    object  ChatList : DestinationScreen ("chatlist")
    object  SingleChat : DestinationScreen ("singlechat/{chatId}"){
        fun createRoute(id: String) = "singlechat/id"
    }

    object  StatusList : DestinationScreen ("statuslist")
    object  SingleStatus : DestinationScreen ("singleStatus/{userId}"){
        fun createRoute(userId: String) = "singleStatus/userId"
    }

}@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatNestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ChatAppNavigation() {
val navController = rememberNavController()
    val vm = hiltViewModel<LCViewModel>()
    NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route ){

        composable(DestinationScreen.SignUp.route){
            SignUpScreen(navController , vm)
        }
        composable(DestinationScreen.ChatList.route){
            ChatListScreen()
        }
        composable(DestinationScreen.Login.route){
            LoginPage(vm , navController)
        }
//        composable(DestinationScreen.SignUp.route){
//            SignUpScreen()
//        }



    }


}
