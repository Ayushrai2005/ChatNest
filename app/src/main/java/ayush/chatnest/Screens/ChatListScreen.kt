package ayush.chatnest.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import ayush.chatnest.LCViewModel

@Composable
fun ChatListScreen(
    navController: NavController , vm : LCViewModel
){

    Column {
Text(text = "Chat List Screen")
        
    }
    BottomNavigationMenu(selectedItem = BottomNavigationItem.CHATLIST, navController =  navController)
}