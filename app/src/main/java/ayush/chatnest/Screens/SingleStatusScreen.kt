package ayush.chatnest.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import ayush.chatnest.CommonImage
import ayush.chatnest.LCViewModel

enum class State {
    INITIAL,
    ACTIVE,
    COMPLETED
}

@Composable
fun CustomProgressIndicator(
    modifier: Modifier, state: State, onComplete: () -> Unit
) {
    var progress = when (state) {
        State.INITIAL -> 0f
        State.ACTIVE -> 0.5f
        State.COMPLETED -> 1f
    }

    if (state == State.ACTIVE) {
        val toggleState = remember { mutableStateOf(false) }

        LaunchedEffect(key1 = toggleState) {
            toggleState.value = true
        }

        val p: Float by animateFloatAsState(
            targetValue = if (toggleState.value) 1f else 0f,
            animationSpec = tween(durationMillis = 5000),
            finishedListener = {
                onComplete.invoke()
            }
        )
        progress = p
    }

    LinearProgressIndicator(modifier = modifier, color = Color.Blue, progress = progress)
}

@Composable
fun SingleStatusScreen(navController: NavController, vm: LCViewModel, userId: String) {
    val status = vm.status.value.filter {
        it.user.userId == userId
    }

    if (status.isNotEmpty()) {
        val currentStatus = remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
        {
            CommonImage(
                data = status[currentStatus.value].imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                status.forEachIndexed() { index, _ ->
                    CustomProgressIndicator(
                        modifier = Modifier.weight(1f),
                        state = when {
                            index < currentStatus.value -> State.COMPLETED
                            index == currentStatus.value -> State.ACTIVE
                            else -> State.INITIAL
                        }
                    ) {
                        if (currentStatus.value < status.size - 1) currentStatus.value ++  else navController.popBackStack()
                    }
                }
            }
        }
    }

}