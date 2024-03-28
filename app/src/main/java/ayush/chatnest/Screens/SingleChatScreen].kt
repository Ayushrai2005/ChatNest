package ayush.chatnest.Screens

import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ayush.chatnest.CommonDivider
import ayush.chatnest.CommonImage
import ayush.chatnest.Data.Message
import ayush.chatnest.LCViewModel

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit, onSearchClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
            .clickable {
                onBackClicked.invoke()
            }
            .padding(8.dp))

        CommonImage(
            data = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(
                    CircleShape
                )
        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
        Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier
            .clickable {
                onSearchClicked.invoke()
            }
            .padding(start = 130.dp))
    }
    Divider(color = Color.Black, thickness = 1.dp)
}

@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    var searchQuery by remember { mutableStateOf("") } // Add this line to create a state variable for the search query
    var isSearchOpen by remember { mutableStateOf(false) } // Add this line to create a state variable for the search bar visibility

    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }
    val myUser = vm.userData.value
    val chatMessages = vm.chatMessages.value.filter { it.message?.contains(searchQuery, ignoreCase = true) == true } // Filter the chatMessages list based on the search query

    LaunchedEffect(
        key1 = Unit
    ) {
        vm.PopulateMessages(chatId)
    }
    BackHandler {
        vm.dePopulate()
        navController.popBackStack()

    }
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    Column {
        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "", onBackClicked = {
            navController.popBackStack()
            vm.dePopulate()
        }, onSearchClicked = {
            isSearchOpen = !isSearchOpen
        })

        if (isSearchOpen) { // Add this block to display the TextField when isSearchOpen is true
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        MessageBox(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessages, // Pass the filtered list of messages to the MessageBox function
            currentUserId = myUser?.userId ?: "",
            currentUserProfilePic = myUser?.imageUrl ?: "",
            otherUserProfilePic = chatUser.imageUrl ?: ""
        )
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }
}

//@Composable
//fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String, currentUserProfilePic: String, otherUserProfilePic: String) {
//    LazyColumn(
//        modifier = modifier
//    ) {
//        items(chatMessages) { msg ->
//            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
//            val color = if (msg.sendBy == currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp), horizontalAlignment = alignment
//            ) {
//                CommonImage(
//                    data = if (msg.sendBy == currentUserId) currentUserProfilePic else otherUserProfilePic,
//                    modifier = Modifier.size(40.dp)
//                )
//                Text(
//                    text = msg.message ?: "",
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(8.dp)) // Modify the shape here
//                        .background(color)
//                        .border(2.dp, Color.Black) // Add a border
//                        .shadow(4.dp, RoundedCornerShape(8.dp)) // Add a shadow
//                        .padding(12.dp),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    style = TextStyle(fontSize = 16.sp) // Change the text style
//                )
//                msg.timeStamp?.let {
//                    if (it.length > 19) {
//                        Text(
//                            text = it.substring(11, 19),
//                            color = Color.White,
//                            fontWeight = FontWeight.Light
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
            .clickable {
                onBackClicked.invoke()
            }
            .padding(8.dp))

        CommonImage(
            data = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(
                    CircleShape
                )
        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))

    }

}

@Composable
fun MessageBox(modifier: Modifier, chatMessages: List<Message>, currentUserId: String, currentUserProfilePic: String, otherUserProfilePic: String) {
    val isDarkTheme = isSystemInDarkTheme()
    val lightColor = Color(0xFF68C400)
    val darkColor = Color(0xFFC0C0C0)
    val textColor = if (isDarkTheme) Color.White else Color.Black

    LazyColumn(
        modifier = modifier
    ) {
        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) if (isDarkTheme) darkColor else lightColor else Color(0xFFC0C0C0)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = if (msg.sendBy == currentUserId) Arrangement.End else Arrangement.Start
            ) {
                CommonImage(
                    data = if (msg.sendBy == currentUserId) currentUserProfilePic else otherUserProfilePic,
                    modifier = Modifier.size(40.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = msg.message ?: "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp)) // Modify the shape here
                            .background(color)
                            .shadow(4.dp, RoundedCornerShape(8.dp)) // Add a shadow
                            .padding(12.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 16.sp) // Change the text style
                    )
                    msg.timeStamp?.let {
                        if (it.length > 19) {
                            Text(
                                text = it.substring(11, 19),
                                color = textColor,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val lightColor = Color(0xFF68C400)
    val darkColor = Color(0xFFC0C0C0)
    val buttonColor = if (isDarkTheme) darkColor else lightColor
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(value = reply, onValueChange = onReplyChange, maxLines = 3 , modifier = Modifier.weight(1f))


            Button(
                onClick = { onSendReply() },
                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor), // Change the color of the button
                border = BorderStroke(2.dp, Color.Black), // Add a border to the button
                shape = RoundedCornerShape(8.dp) // Change the shape of the button
            ) {
                Text(
                    text = "Send",
                    color = textColor, // Change the text color
                    fontWeight = FontWeight.Bold // Change the font weight of the button text
                )
            }
        }
    }
}