package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color


@Composable
fun FindDuoPartnerScreen(onNext: () -> Unit) {
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(colorStops = colorStops))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Link with your friend",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left
                ),
                modifier = Modifier.fillMaxSize()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your partner's phone number to start double dating together.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxSize()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Partner's Phone Number",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Left
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            var text by remember {mutableStateOf("")}

            //TEMPORARY PLACEHOLDERS FOR ERROR AND SUCCESS SNACKBARS
            val errorSnackbarHostState = remember { SnackbarHostState() }
            //val scope = rememberCoroutineScope()
            val sentSnackbarHostState = remember { SnackbarHostState() }

            val joinedSnackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                errorSnackbarHostState.showSnackbar(
                    message = "User not found. Invite them to Duo Dating or check the number.",
                    duration = SnackbarDuration.Indefinite // stays until dismissed
                )
            }

            LaunchedEffect(Unit) {
                sentSnackbarHostState.showSnackbar(
                    message = "Request sent! We'll let you know when they accept.",
                    duration = SnackbarDuration.Indefinite
                )
            }

            LaunchedEffect(Unit) {
                joinedSnackbarHostState.showSnackbar(
                    message = "Friend joined!",
                    duration = SnackbarDuration.Indefinite
                )
            }
            //End of temporary placeholder

            // Partner phone number invitation
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(
                            text = "+1(555)000-0000",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Left
                            )
                        )},
                    trailingIcon = {
                        Button(
                            onClick = { /*handle send */},
                            modifier = Modifier.padding(end = 8.dp)
                        ){
                            Text("Send")
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Temporary placeholder for the error message
                SnackbarHost(hostState = errorSnackbarHostState){ data ->
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxSize().height(74.dp)
                    ){
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = data.visuals.message,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Request sent message
                SnackbarHost(hostState = sentSnackbarHostState) { data ->
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxSize().height(74.dp)
                    ){
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = data.visuals.message,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Friend Joined Message
                SnackbarHost(hostState = joinedSnackbarHostState) { data ->
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF054D21),
                            contentColor = Color(0xFF00D761)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFF00D761)),
                        modifier = Modifier.fillMaxSize().height(74.dp)
                    ){
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF00D761),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = data.visuals.message,
                                color = Color(0xFF00D761),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onNext, modifier = Modifier.fillMaxSize().height(60.dp)) {
                Text(text = "Link & Continue",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
