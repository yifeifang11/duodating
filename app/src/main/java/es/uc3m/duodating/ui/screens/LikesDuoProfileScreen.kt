package es.uc3m.duodating.ui.screens
import es.uc3m.duodating.ui.viewmodels.LikedDuoProfileViewModel
import es.uc3m.duodating.data.models.DuoWithUsers
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uc3m.duodating.ui.viewmodels.LikesViewModel

@Composable
fun LikesDuoProfileScreen(
    duoId: String,
    viewModel: LikedDuoProfileViewModel = viewModel(),
    likesViewModel: LikesViewModel,
    onBack: () -> Unit
    ) {

    val duo by viewModel.duo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(duoId){
        viewModel.loadDuo(duoId)
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error ?: "Error")
        }
        return
    }

    val data = duo ?: return

    DuoProfileContent(
        data = data,
        onLike = {
            viewModel.likeBack(duoId){
                likesViewModel.removeDuo(duoId)
                onBack()
            }
        },
        onPass = {
            viewModel.pass(data.duo.duoId!!){
                likesViewModel.removeDuo(duoId)
                onBack()
            }
        }
    )
}
@Composable
fun DuoProfileContent(
    data: DuoWithUsers,
    onLike: () -> Unit,
    onPass: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Duo Card (same as Discover)
            item {
                InfoCard(
                    names = "${data.user1.firstName} & ${data.user2.firstName}",
                    prompt = data.duo.questionChoice,
                    response = data.duo.questionAnswer,
                    imageUrl = data.duo.photoUrl
                )
            }

            // User 1
            item {
                InfoCard(
                    names = data.user1.firstName,
                    prompt = data.user1.questionChoice,
                    response = data.user1.questionAnswer,
                    imageUrl = data.user1.photoUrl
                )
            }

            // User 2
            item {
                InfoCard(
                    names = data.user2.firstName,
                    prompt = data.user2.questionChoice,
                    response = data.user2.questionAnswer,
                    imageUrl = data.user2.photoUrl
                )
            }
        }

        // ACTION BUTTONS (identical style to Discover)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            IconButton(
                onClick = onPass,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                        RoundedCornerShape(32.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pass",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onLike,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(32.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}