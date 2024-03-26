package ayush.chatnest.Screens

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ayush.chatnest.CommonDivider
import ayush.chatnest.CommonImage
import ayush.chatnest.LCViewModel
import ayush.chatnest.commonProgressBar
import java.io.IOException


@Composable
fun ProfileScreen(navController: NavController, vm: LCViewModel){

    val inProgress = vm.inProgress.value
    if(inProgress){
        commonProgressBar()
    }
    else{
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileContent(modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
                vm = vm ,
                name = "",
                number = "",
                onNameChange = {""},
                onNumberChange = {""},
                onBack = {},
                onLogOut = {},
                onSave = {}
            )
            BottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE, navController = navController)

        }
    }
}

@Composable
fun ProfileContent(
    modifier : Modifier,
    vm : LCViewModel,
    name : String ,
    number : String,
    onNameChange : (String) ->Unit ,
    onNumberChange : (String) -> Unit ,
    onBack : () ->Unit  ,
    onSave:() ->Unit,
    onLogOut:() ->Unit
){

    val imageUrl = vm.userData.value?.imageUrl

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp) ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back" , Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save" , Modifier.clickable {
                onSave.invoke()
            })
        }
        CommonDivider()
        ProfileImage( imageUrl = imageUrl , vm = vm)
        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name"  , modifier = Modifier.width(100.dp) )
            TextField(value = name, onValueChange = onNameChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent


                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Phone Number" , Modifier.width(100.dp))
            TextField(value = number, onValueChange = onNumberChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent


                )
            )
        }
        CommonDivider()
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = "Logout", Modifier.clickable {onLogOut.invoke() })
        }
    }


}

@Composable
fun ProfileImage(imageUrl : String? , vm : LCViewModel){
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)){

        Column(modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .clickable {
                launcher.launch("image/*")
            } , horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                    CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Picture")

        }

        if(vm.inProgress.value){
            commonProgressBar()
        }
    }
    val context = LocalContext.current

}

//private fun loadBitmap(contentResolver: ContentResolver, uri: Uri): ImageBitmap {
//    return try {
//        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true)
//        resizedBitmap.asImageBitmap()
//    } catch (e: IOException) {
//        ImageBitmap(1, 1)
//    }
//}
