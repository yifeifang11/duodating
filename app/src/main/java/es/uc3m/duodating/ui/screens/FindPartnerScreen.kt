package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.uc3m.duodating.ui.viewmodels.DuoViewModel

@Composable
fun FindPartnerScreen(
    viewModel: DuoViewModel,
    onInviteSent: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )

    // Optional: Observe status to trigger navigation on success
    LaunchedEffect(viewModel.currentUser?.status) {
        if (viewModel.currentUser?.status == "WAITING") {
            onInviteSent()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(colorStops = colorStops))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Who's your duo?",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Enter your bestie's phone number to invite them to Duo Dating.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Partner's Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { 
                        viewModel.sendInvite(phone)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = phone.isNotBlank()
                ) {
                    Text("Send Invite")
                }
            }
            
            viewModel.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
