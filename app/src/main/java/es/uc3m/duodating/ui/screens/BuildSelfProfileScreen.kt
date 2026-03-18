package es.uc3m.duodating.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BuildSelfProfileScreen(onNext: () -> Unit) {
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPrompt by remember { mutableStateOf("My most irrational fear is...") }
    var promptResponse by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(colorStops = colorStops))
    ){
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Show your best self", style = MaterialTheme.typography.displaySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This is how your profile will look to potential matches",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    //textAlign = TextAlign.Center
                ),
                //modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            SelfProfileEditContent(
                selectedUri = selectedUri,
                onUriChange = { selectedUri = it },
                selectedPrompt = selectedPrompt,
                onPromptChange = { selectedPrompt = it },
                promptResponse = promptResponse,
                onResponseChange = { promptResponse = it },
                onSave = onNext
            )
        }
    }
}

@Composable
fun SelfProfileEditContent(
    modifier: Modifier = Modifier,
    selectedUri: Uri?,
    onUriChange: (Uri?) -> Unit,
    selectedPrompt: String,
    onPromptChange: (String) -> Unit,
    promptResponse: String,
    onResponseChange: (String) -> Unit,
    onSave: () -> Unit,
    buttonText: String = "Next"
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onUriChange(uri)
    }
            Spacer(modifier = Modifier.height(16.dp))

            val selectedUri = remember { mutableStateOf<Uri?>(null) }

            val photoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                selectedUri.value = uri
                // uri is null if user cancelled
                // store it in a viewmodel or local state
            }

    val menuItemData = listOf(
        "My most irrational fear is...",
        "The way to my heart is...",
        "I'm actually a pro at...",
        "I'm looking for someone who...",
        "My favorite travel story is...",
        "The best gift I've ever received...",
        "On a typical Sunday, you can find me..."
    )
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add a photo",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Left
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(456.dp),
            shape = RoundedCornerShape(10),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (selectedUri != null) "Photo selected ✓" else "Tap to upload",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Icebreaker",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Left
            ),
            modifier = Modifier.fillMaxWidth()
        )
            //Choose a prompt
            Text(
                text = "Choose a prompt",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Left
                ),
                modifier = Modifier.fillMaxWidth()
            )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose a prompt",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Left
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            )
            {
                Text(
                    text = selectedPrompt,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Left
            val expanded = remember { mutableStateOf(false) }
            //Placeholder
            val menuItemData = List(10) { "Option ${it + 1}" }
            val selectedText = remember { mutableStateOf(menuItemData[0]) }

            Box {
                OutlinedButton(
                    onClick = { expanded.value = true },
                    modifier = Modifier.fillMaxSize().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItemData.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onPromptChange(option)
                            expanded = false
                        }
                    )
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier.height(56.dp)
                ) {
                    menuItemData.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedText.value = option
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your answer",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Left
            ),
            modifier = Modifier.fillMaxWidth()
        )
            var text by remember {mutableStateOf("")}

            Box {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(
                            text = "Type your answer here",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Left
                            )
                        )},
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = promptResponse,
            onValueChange = onResponseChange,
            placeholder = {
                Text(
                    text = "Type your answer here",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Left
                    )
                )},
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text(text = buttonText)

            Button(onClick = onNext, modifier = Modifier.fillMaxSize().height(60.dp)) {
                Text(text = "Next",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
