package es.uc3m.duodating.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun ProfileTabScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Default to Edit tab
    val tabs = listOf("Preview", "Edit")

    // Combined/Duo Profile State
    var duoName1 by remember { mutableStateOf("Alice") }
    var duoName2 by remember { mutableStateOf("Anna") }
    var duoUri by remember { mutableStateOf<Uri?>(null) }
    var duoPrompt by remember { mutableStateOf("How we met...") }
    var duoResponse by remember { mutableStateOf("We met at a coffee shop!") }

    // Individual Profile State (Current User)
    var selfName by remember { mutableStateOf("Alice") }
    var selfUri by remember { mutableStateOf<Uri?>(null) }
    var selfPrompt by remember { mutableStateOf("My most irrational fear is...") }
    var selfResponse by remember { mutableStateOf("Spiders, definitely spiders.") }

    // Partner Profile State (Mock data for preview)
    val partnerName = "Anna"
    val partnerPrompt = "I'm looking for someone who..."
    val partnerResponse = "Likes hiking as much as I do."

    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(colorStops = colorStops))
    ) {
        // Fixed TabRow at the top
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { 
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    }
                )
            }
        }

        // Content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Preview Tab: Showing the full stack of cards
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 1. Combined Duo Card
                            InfoCard(
                                names = "$duoName1 & $duoName2",
                                prompt = duoPrompt,
                                response = duoResponse,
                                imageRes = null
                            )
                            
                            // 2. Self Card
                            InfoCard(
                                names = selfName,
                                prompt = selfPrompt,
                                response = selfResponse,
                                imageRes = null
                            )

                            // 3. Partner Card
                            InfoCard(
                                names = partnerName,
                                prompt = partnerPrompt,
                                response = partnerResponse,
                                imageRes = null
                            )
                        }
                    }
                    1 -> {
                        // Edit Tab: Combined and Individual Profile Editing
                        Column(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Edit Duo Profile",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DuoProfileEditContent(
                                selectedUri = duoUri,
                                onUriChange = { duoUri = it },
                                selectedPrompt = duoPrompt,
                                onPromptChange = { duoPrompt = it },
                                promptResponse = duoResponse,
                                onResponseChange = { duoResponse = it },
                                onSave = { 
                                    // Switch to preview to see changes
                                    selectedTabIndex = 0
                                },
                                buttonText = "Save Changes"
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )

                            Text(
                                text = "Edit Personal Profile",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            SelfProfileEditContent(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                selectedUri = selfUri,
                                onUriChange = { selfUri = it },
                                selectedPrompt = selfPrompt,
                                onPromptChange = { selfPrompt = it },
                                promptResponse = selfResponse,
                                onResponseChange = { selfResponse = it },
                                onSave = { 
                                    // Switch to preview to see changes
                                    selectedTabIndex = 0
                                },
                                buttonText = "Save Changes"
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}
