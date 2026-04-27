package es.uc3m.duodating.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun signUp(phone: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Formatting the phone number into a pseudo-email for Email/Password provider
                // Ensure "Email/Password" is ENABLED in Firebase Console > Authentication > Sign-in method
                val cleanPhone = phone.filter { it.isDigit() || it == '+' }
                val email = "$cleanPhone@duodating.com"
                
                auth.createUserWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: FirebaseAuthException) {
                errorMessage = when (e.errorCode) {
                    "ERROR_CONFIGURATION_NOT_FOUND" -> "Auth provider not enabled in Firebase Console. Please enable Email/Password."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "This phone number is already registered."
                    "ERROR_WEAK_PASSWORD" -> "The password is too weak."
                    else -> e.localizedMessage
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}
