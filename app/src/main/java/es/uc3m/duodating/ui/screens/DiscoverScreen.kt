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
import coil.compose.AsyncImage
import es.uc3m.duodating.R
import es.uc3m.duodating.data.models.DuoProfile
import es.uc3m.duodating.ui.theme.DarkPink
import es.uc3m.duodating.ui.theme.HotPink
import es.uc3m.duodating.ui.theme.White

@Composable
fun DiscoverScreen() {
    val profiles = remember {
        listOf(
            DuoProfile(
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
            ),
            DuoProfile(
                user1id = "3",
                user1name = "Chloe",
                user2id = "4",
                user2name = "Diana",
                user1prompt = "Fact about me",
                user1promptresponse = "I have a collection of over 50 vintage vinyls.",
                user2prompt = "Dating me is like...",
                user2promptresponse = "Finding a \$20 bill in your old jeans.",
                user1image = "alice",
                user2image = "alice",
                combinedprompt = "We both love",
                combinedpromptresponse = "Late night karaoke and terrible singing.",
                combinedimage = "alice_and_anna"
            ),
            DuoProfile(
                user1id = "5",
                user1name = "Eva",
                user2id = "6",
                user2name = "Fiona",
                user1prompt = "Green flag if...",
                user1promptresponse = "You know how to cook a decent carbonara.",
                user2prompt = "Worst idea I ever had",
                user2promptresponse = "Attempting to cut my own bangs during lockdown.",
                user1image = "alice",
                user2image = "alice",
                combinedprompt = "Our friendship in a nutshell",
                combinedpromptresponse = "Chaos, laughter, and constant coffee runs.",
                combinedimage = "alice_and_anna"
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val likedProfiles = remember { mutableStateListOf<String>() }
    val passedProfiles = remember { mutableStateListOf<String>() }

    if (currentIndex < profiles.size) {
        val currentDuo = profiles[currentIndex]

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    InfoCard(
                        names = "${currentDuo.user1name} & ${currentDuo.user2name}",
                        prompt = currentDuo.combinedprompt,
                        response = currentDuo.combinedpromptresponse,
                        imageRes = R.drawable.alice_and_anna
                    )
                }
                item {
                    InfoCard(
                        names = currentDuo.user1name,
                        prompt = currentDuo.user1prompt,
                        response = currentDuo.user1promptresponse,
                        imageRes = R.drawable.alice
                    )
                }
                item {
                    InfoCard(
                        names = currentDuo.user2name,
                        prompt = currentDuo.user2prompt,
                        response = currentDuo.user2promptresponse,
                        imageRes = R.drawable.alice
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
                        passedProfiles.add(currentDuo.user1id + "_" + currentDuo.user2id)
                        currentIndex++
                    },
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
                    onClick = {
                        likedProfiles.add(currentDuo.user1id + "_" + currentDuo.user2id)
                        currentIndex++
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(HotPink.copy(alpha = 0.9f), RoundedCornerShape(32.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    } else {
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
                    text = "No more profiles to show right now!",
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
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { currentIndex = 0 }) {
                    Text("Start Over (Demo)")
                }
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
