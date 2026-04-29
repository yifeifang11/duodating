package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import es.uc3m.duodating.R
import es.uc3m.duodating.ui.theme.DarkPink
import es.uc3m.duodating.ui.theme.HotPink
import es.uc3m.duodating.ui.theme.White
import es.uc3m.duodating.ui.viewmodels.DuoViewModel

@Composable
fun DiscoverScreen(
    viewModel: DuoViewModel = viewModel()
) {
    val availableDuos = viewModel.availableDuos
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // Local index to show one duo at a time (though the flow filters them out once liked)
    var currentIndex by remember { mutableIntStateOf(0) }

    // Reset index if the list changes significantly
    LaunchedEffect(availableDuos.size) {
        if (currentIndex >= availableDuos.size) {
            currentIndex = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && availableDuos.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (availableDuos.isNotEmpty() && currentIndex < availableDuos.size) {
            val currentDuo = availableDuos[currentIndex]

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    InfoCard(
                        names = "The Duo",
                        prompt = currentDuo.questionChoice,
                        response = currentDuo.questionAnswer,
                        imageUrl = currentDuo.photoUrl
                    )
                }
            }

            // Floating Interaction Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        currentIndex++
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f), RoundedCornerShape(32.dp)),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Pass",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = {
                            viewModel.likeDuo(currentDuo.duoId) {
                                // Optional: handle match animation/navigation
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(HotPink.copy(alpha = 0.9f), RoundedCornerShape(32.dp)),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = HotPink,
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        } else if (!isLoading) {
            // No more profiles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SentimentDissatisfied,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No more duos found nearby!",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Check back later or expand your preferences.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }

        errorMessage?.let {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                action = {
                    TextButton(onClick = { /* Clear error in VM if needed */ }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(it)
            }
        }
    }
}

@Composable
fun InfoCard(
    names: String,
    prompt: String,
    response: String,
    imageUrl: String? = null,
    imageRes: Int? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .background(Color.Gray)
            ) {
                if (imageUrl != null && imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp).align(Alignment.Center),
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 800f
                            )
                        )
                )
                
                Text(
                    text = names,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                )
            }

            // Prompt and Response Section with DarkPink background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkPink)
                    .padding(20.dp)
            ) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = response,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
