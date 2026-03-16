package es.uc3m.duodating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import es.uc3m.duodating.ui.navigation.NavGraph
import es.uc3m.duodating.ui.theme.DuoDatingTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import es.uc3m.duodating.ui.screens.WelcomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DuoDatingTheme (darkTheme = true) {
                NavGraph()
            }
        }
    }
}
