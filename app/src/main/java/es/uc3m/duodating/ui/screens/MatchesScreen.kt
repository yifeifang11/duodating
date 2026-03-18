package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun MatchesScreen(onProfileClick: () -> Unit = {}) {
    val matches = remember {
        listOf(
            DuoProfile(user1id = "1", user2id = "2", user1name = "Alice", user2name = "Anna", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "3", user2id = "4", user1name = "Chloe", user2name = "Diana", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "5", user2id = "6", user1name = "Eva", user2name = "Fiona", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "7", user2id = "8", user1name = "Grace", user2name = "Heidi", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "9", user2id = "10", user1name = "Ivy", user2name = "Jade", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "11", user2id = "12", user1name = "Kara", user2name = "Lola", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "13", user2id = "14", user1name = "Mia", user2name = "Nora", combinedimage = "alice_and_anna"),
            DuoProfile(user1id = "15", user2id = "16", user1name = "Olive", user2name = "Paige", combinedimage = "alice_and_anna")
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Likes",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(matches) { match ->
                    MatchGridItem(match, onProfileClick)
                }
            }
        }
    }
}

@Composable
fun MatchGridItem(match: DuoProfile, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Gray)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.alice_and_anna),
            contentDescription = "${match.user1name} & ${match.user2name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 200f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = "${match.user1name} & ${match.user2name}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
