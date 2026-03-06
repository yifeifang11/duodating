package com.example.duodating.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Duo Dating", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onCreateAccountClick) {
            Text(text = "Create Account")
        }
        TextButton(onClick = onLoginClick) {
            Text(text = "Already have an account? Login")
        }
    }
}
