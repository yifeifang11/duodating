package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.duodating.R
import es.uc3m.duodating.data.models.Chat

@Composable
fun ChatsScreen(onChatClick: () -> Unit = {}) {
    val dummyChats = remember {
        listOf(
            Chat(
                user1image = "alice",
                user2image = "alice",
                user1name = "Sarah",
                user2name = "Emma",
                user3name = "James",
                lastMessage = "let's plan this date!"
            ),
            Chat(
                user1image = "alice",
                user2image = "alice",
                user1name = "Chloe",
                user2name = "Diana",
                user3name = "James",
                lastMessage = "That sounds like a lot of fun, can't wait!"
            ),
            Chat(
                user1image = "alice",
                user2image = "alice",
                user1name = "Eva",
                user2name = "Fiona",
                user3name = "James",
                lastMessage = "Where should we meet?"
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Messages",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(dummyChats) { chat ->
                    ChatItem(chat, onChatClick)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Double Rounded Images
        Box(modifier = Modifier.size(60.dp)) {
            // First image (top left-ish)
            Image(
                painter = painterResource(id = R.drawable.alice), // Using alice as placeholder
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            // Second image (bottom right-ish)
            Image(
                painter = painterResource(id = R.drawable.alice), // Using alice as placeholder
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .padding(2.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Title: user1name, user2name, user3name, and "you"
            Text(
                text = "${chat.user1name}, ${chat.user2name}, ${chat.user3name} & you",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            // Last message: "user1name: lastMessage"
            Text(
                text = "${chat.user1name}: ${chat.lastMessage}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
