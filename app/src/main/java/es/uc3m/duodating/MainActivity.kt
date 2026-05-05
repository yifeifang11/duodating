package es.uc3m.duodating

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import es.uc3m.duodating.data.UserRepository
import es.uc3m.duodating.ui.navigation.NavGraph
import es.uc3m.duodating.ui.theme.DuoDatingTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    // Initialize your repository (adjust if you use Dependency Injection like Hilt)
    private val userRepository = UserRepository()

    // 1. Define the permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getAndSaveToken()
        } else {
            Log.w("FCM", "Notifications permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()
        // 2. Check and request permissions
        checkNotificationPermission()

        setContent {
            DuoDatingTheme(darkTheme = true) {
                NavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // This tells the activity to update its current intent so that
        // the NavGraph can "see" the new deep link URI.
        setIntent(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Chat Notifications"
            val descriptionText = "Notifications for new duo messages"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("chat_notifications", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getAndSaveToken()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 12 and below don't need runtime permission
            getAndSaveToken()
        }
    }

    private fun getAndSaveToken() {
        lifecycleScope.launch {
            try {
                // Fetch the token
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("FCM_TEST", "Current Token: $token")

                // Update Firestore
                userRepository.updateFcmToken(token)
                Log.d("FCM_TEST", "Token saved to Firestore successfully")
            } catch (e: Exception) {
                Log.e("FCM_TEST", "Error fetching/saving token", e)
            }
        }
    }
}