package ayush.chatnest.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import ayush.chatnest.DestinationScreen
import ayush.chatnest.R
import ayush.chatnest.navigateTo


enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {

    CHATLIST(R.drawable.chat, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.application, DestinationScreen.StatusList),
    PROFILE(R.drawable.user, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(top = 4.dp)
            .background(Color.LightGray)
            .border(1.dp, Color.Black)
            .shadow(4.dp) ,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        for(item in BottomNavigationItem.values()){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription =  null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            navigateTo(navController , item.navDestination.route)
                        },
                    colorFilter = if(item == selectedItem)
                        ColorFilter.tint(color = Color.Black)
                    else
                        ColorFilter.tint(Color.Gray)
                )
                Text(text = when (item) {
                    BottomNavigationItem.CHATLIST -> "Chats"
                    BottomNavigationItem.STATUSLIST -> "Updates"
                    BottomNavigationItem.PROFILE -> "Profile"
                }, textAlign = TextAlign.Center)
            }
        }
    }
}