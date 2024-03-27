package ayush.chatnest.Screens

import android.app.Activity
import android.graphics.drawable.Icon
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ayush.chatnest.CommonDivider
import ayush.chatnest.CommonRow
import ayush.chatnest.DestinationScreen
import ayush.chatnest.LCViewModel
import ayush.chatnest.TitleText
import ayush.chatnest.commonProgressBar
import ayush.chatnest.navigateTo

@Composable
fun StatusScreen(navController: NavController, vm: LCViewModel) {
    val inProgress = vm.inProgressStatus
    if (inProgress.value) {
        commonProgressBar()
    } else {
        val status = vm.status.value
        val userData = vm.userData.value
        val myStatus = status.filter { it.user.userId == userData?.userId }
        val otherStatus = status.filter { it.user.userId != userData?.userId }
        val onDismiss: () -> Unit = {
            // do nothing
        }
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                // Handle the returned Uri
                    uri ->
                uri?.let {
                    // Handle the returned Uri
                    vm.uploadStatus(it)
                }

            }
        Scaffold(
            topBar = {
                TitleText("Status")
            },
            floatingActionButton = {
                FAB{
                    launcher.launch("image/*")
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    if (status.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Status Found",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        if (myStatus.isNotEmpty()) {
                            myStatus[0].user.name?.let { it1 ->
                                CommonRow(
                                    imageUrl = myStatus[0].user.imageUrl,
                                    name = it1
                                ) {
                                    navigateTo(
                                        navController,
                                        DestinationScreen.SingleStatus.createRoute(myStatus[0].user.userId!!)
                                    )
                                }
                                CommonDivider()
                                val uniqueUsers = otherStatus.map { it.user }.toSet().toList()
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
                                    items(uniqueUsers.size) { index ->
                                        val user = uniqueUsers[index]
                                        val userStatus =
                                            otherStatus.filter { it.user.userId == user.userId }
                                        user.name?.let { it1 ->
                                            CommonRow(
                                                imageUrl = user.imageUrl,
                                                name = it1
                                            ) {
                                                navigateTo(
                                                    navController,
                                                    DestinationScreen.SingleStatus.createRoute(user.userId!!)
                                                )
                                            }
                                        }
                                        userStatus.forEach {
                                            CommonRow(
                                                imageUrl = it.imageUrl,
                                                name = it.timeStamp!!
                                            ) {
                                                navigateTo(
                                                    navController,
                                                    DestinationScreen.SingleStatus.createRoute(it.user.userId!!)
                                                )
                                            }
                                        }
                                        CommonDivider()
                                    }
                                }
                            }
                        }
                    }
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.STATUSLIST,
                        navController = navController
                    )
                }

            }
        )
    }
}

@Composable
fun FAB(
    onFabClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Add", tint = Color.White)
    }
}