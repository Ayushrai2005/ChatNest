package ayush.chatnest.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import ayush.chatnest.LCViewModel


@Composable
fun StatusScreen(navController: NavController , vm:LCViewModel){

    BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUSLIST, navController = navController )

}