package com.example.duodating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.duodating.ui.navigation.NavGraph
import com.example.duodating.ui.theme.DuoDatingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DuoDatingTheme {
                NavGraph()
            }
        }
    }
}
