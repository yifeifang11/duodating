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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.duodating.R
import es.uc3m.duodating.data.models.DuoProfile
import es.uc3m.duodating.ui.theme.DarkPink

@Composable
fun DiscoverScreen() {
    // Only one duo for now
    val currentDuo = DuoProfile(
        user1id = "1",
        user1name = "Alice",
        user2id = "2",
        user2name = "Anna",
        user1prompt = "My favorite travel memory",
        user1promptresponse = "Seeing the Northern Lights in Iceland!",
        user2prompt = "I'm looking for...",
        user2promptresponse = "Someone to go hiking with every weekend.",
        user1image = "alice",
        user2image = "alice",
        combinedprompt = "Our ideal Sunday",
        combinedpromptresponse = "Brunch followed by a long walk in the park.",
        combinedimage = "alice_and_anna"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Card 1: Combined Info
                InfoCard(
                    names = "${currentDuo.user1name} & ${currentDuo.user2name}",
                    prompt = currentDuo.combinedprompt,
                    response = currentDuo.combinedpromptresponse,
                    imageRes = R.drawable.alice_and_anna
                )
            }
            item {
                // Card 2: User 1 Info
                InfoCard(
                    names = currentDuo.user1name,
                    prompt = currentDuo.user1prompt,
                    response = currentDuo.user1promptresponse,
                    imageRes = R.drawable.alice
                )
            }
            item {
                // Card 3: User 2 Info
                InfoCard(
                    names = currentDuo.user2name,
                    prompt = currentDuo.user2prompt,
                    response = currentDuo.user2promptresponse,
                    imageRes = R.drawable.alice
                )
            }
        }

        // Floating Interaction Buttons (Sticky at bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { /* Pass logic */ },
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f), RoundedCornerShape(32.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pass",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = { /* Like logic */ },
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f), RoundedCornerShape(32.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    names: String,
    prompt: String,
    response: String,
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
                if (imageRes != null) {
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
