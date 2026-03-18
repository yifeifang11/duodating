package es.uc3m.duodating.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation


@Composable
fun CreateAccountScreen(onAccountCreated: () -> Unit) {
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.background,
        0.5f to MaterialTheme.colorScheme.secondary
    )
    Box(
        modifier = Modifier
            //.padding(horizontal = 40.dp)
            .fillMaxSize()
            .background(Brush.horizontalGradient(colorStops = colorStops)))
    {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Join the fun with your bestie", style = MaterialTheme.typography.displaySmall.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Set up your account to start matching with other pairs and double your dating life",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    //textAlign = TextAlign.Center
                ),
                //modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            var text by remember {mutableStateOf("")}
            OutlinedTextField(
                value = text,
                shape = RoundedCornerShape(26.dp),
                onValueChange = {text = it},
                //state = rememberTextFieldState(initialText = "+1(555)000-0000"),
                label = { Text("Phone Number")},
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            var password by remember { mutableStateOf("") }

            OutlinedTextField(
                value = password,
                shape = RoundedCornerShape(26.dp),
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "By signing up, you agree to our Terms of Service and Privacy Policy.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(133.dp))

            Button(onClick = onAccountCreated, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text(text = "Create Account",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

}
