package es.uc3m.duodating.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import es.uc3m.duodating.ui.viewmodels.DuoViewModel
import es.uc3m.duodating.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileTabScreen(
    onLogout: () -> Unit,
    duoViewModel: DuoViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Default to Edit tab
    val tabs = listOf("Preview", "Edit")

    val currentUser = duoViewModel.currentUser
    val currentDuo = duoViewModel.currentDuo
    val partnerUser = duoViewModel.partnerUser

    // Combined/Duo Profile State
    var duoName1 by remember { mutableStateOf("") }
    var duoName2 by remember { mutableStateOf("") }
    var duoUri by remember { mutableStateOf<Uri?>(null) }
    var duoPrompt by remember { mutableStateOf("How we met...") }
    var duoResponse by remember { mutableStateOf("") }
    var duoPhotoUrl by remember { mutableStateOf("") }

    // Individual Profile State (Current User)
    var selfFirstName by remember { mutableStateOf("") }
    var selfLastName by remember { mutableStateOf("") }
    var selfUri by remember { mutableStateOf<Uri?>(null) }
    var selfPrompt by remember { mutableStateOf("My most irrational fear is...") }
    var selfResponse by remember { mutableStateOf("") }
    var selfPhotoUrl by remember { mutableStateOf("") }

    // Partner Profile State
    var partnerFirstName by remember { mutableStateOf("") }
    var partnerLastName by remember { mutableStateOf("") }
    var partnerPrompt by remember { mutableStateOf("") }
    var partnerResponse by remember { mutableStateOf("") }
    var partnerPhotoUrl by remember { mutableStateOf("") }

    // Detected changes
    val hasDuoChanges = remember(currentDuo, duoUri, duoPrompt, duoResponse) {
        currentDuo != null && (
            duoPrompt != currentDuo.questionChoice ||
            duoResponse != currentDuo.questionAnswer ||
            duoUri != null
        )
    }

    val hasPersonalChanges = remember(currentUser, selfFirstName, selfLastName, selfUri, selfPrompt, selfResponse) {
        currentUser != null && (
            selfFirstName != currentUser.firstName ||
            selfLastName != currentUser.lastName ||
            selfPrompt != currentUser.questionChoice ||
            selfResponse != currentUser.questionAnswer ||
            selfUri != null
        )
    }

    // Sync state with ViewModel data
    LaunchedEffect(currentUser, currentDuo, partnerUser) {
        currentUser?.let {
            selfFirstName = it.firstName
            selfLastName = it.lastName
            selfPrompt = it.questionChoice
            selfResponse = it.questionAnswer
            selfPhotoUrl = it.photoUrl
        }
        currentDuo?.let {
            duoPrompt = it.questionChoice
            duoResponse = it.questionAnswer
            duoPhotoUrl = it.photoUrl
        }
        partnerUser?.let {
            partnerFirstName = it.firstName
            partnerLastName = it.lastName
            partnerPrompt = it.questionChoice
            partnerResponse = it.questionAnswer
            partnerPhotoUrl = it.photoUrl
        }
        
        if (currentUser != null && partnerUser != null) {
            duoName1 = currentUser.firstName
            duoName2 = partnerUser.firstName
        }
    }

    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(colorStops = colorStops))
    ) {
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
                                imageUrl = duoPhotoUrl
                            )
                            
                            // 2. Self Card
                            InfoCard(
                                names = "$selfFirstName $selfLastName",
                                prompt = selfPrompt,
                                response = selfResponse,
                                imageUrl = selfPhotoUrl
                            )

                            // 3. Partner Card
                            InfoCard(
                                names = "$partnerFirstName $partnerLastName",
                                prompt = partnerPrompt,
                                response = partnerResponse,
                                imageUrl = partnerPhotoUrl
                            )
                        }
                    }
                    1 -> {
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
                                    val duoId = currentUser?.linkedDuoId
                                    if (duoId != null) {
                                        duoViewModel.saveDuoProfile(
                                            duoId = duoId,
                                            questionChoice = duoPrompt,
                                            questionAnswer = duoResponse,
                                            imageUri = duoUri,
                                            onSuccess = { selectedTabIndex = 0 }
                                        )
                                    }
                                },
                                buttonText = "Save Duo Changes",
                                isEnabled = hasDuoChanges
                            )

                            HorizontalDivider(
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
                                firstName = selfFirstName,
                                onFirstNameChange = { selfFirstName = it },
                                lastName = selfLastName,
                                onLastNameChange = { selfLastName = it },
                                selectedUri = selfUri,
                                onUriChange = { selfUri = it },
                                selectedPrompt = selfPrompt,
                                onPromptChange = { selfPrompt = it },
                                promptResponse = selfResponse,
                                onResponseChange = { selfResponse = it },
                                onSave = { 
                                    profileViewModel.saveProfile(
                                        firstName = selfFirstName,
                                        lastName = selfLastName,
                                        dob = currentUser?.dob ?: "01/01/2000",
                                        phoneNumber = currentUser?.phoneNumber ?: "",
                                        questionChoice = selfPrompt,
                                        questionAnswer = selfResponse,
                                        imageUri = selfUri,
                                        onSuccess = { selectedTabIndex = 0 }
                                    )
                                },
                                buttonText = "Save Personal Changes",
                                isEnabled = hasPersonalChanges
                            )
                            
                            Spacer(modifier = Modifier.height(48.dp))
                            
                            Button(
                                onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    onLogout()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Log Out", style = MaterialTheme.typography.bodyLarge)
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}
