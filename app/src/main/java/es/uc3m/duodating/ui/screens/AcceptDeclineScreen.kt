package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.uc3m.duodating.ui.viewmodels.DuoViewModel

@Composable
fun AcceptDeclineScreen(
    viewModel: DuoViewModel,
    onAccepted: () -> Unit
) {
    val invite = viewModel.incomingInvite
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )

    // Observe status to navigate when accepted
    LaunchedEffect(viewModel.currentUser?.status) {
        if (viewModel.currentUser?.status == "LINKED") {
            onAccepted()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colorStops = colorStops))
    ) {
        if (invite == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No pending invites found.")
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New Duo Request!",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${invite.senderName} wants to be your duo partner!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (viewModel.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { viewModel.acceptInvite() },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Accept & Start Matching")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.declineInvite() },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Decline")
                    }
                }
            }
        }
    }
}
