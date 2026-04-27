package es.uc3m.duodating.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import es.uc3m.duodating.data.models.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private fun cleanPhone(phone: String) = phone.filter { it.isDigit() || it == '+' }

    fun signUp(phone: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val cleaned = cleanPhone(phone)
                val email = "$cleaned@duodating.com"
                
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("Failed to get UID")
                
                // Initialize user document with ONBOARDING status and CLEAN phone number
                val user = User(
                    uid = uid,
                    phoneNumber = cleaned,
                    status = "ONBOARDING"
                )
                firestore.collection("users").document(uid).set(user).await()
                
                onSuccess()
            } catch (e: FirebaseAuthException) {
                errorMessage = e.localizedMessage
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun login(phone: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val cleaned = cleanPhone(phone)
                val email = "$cleaned@duodating.com"
                
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("Failed to get UID")
                
                val snapshot = firestore.collection("users").document(uid).get().await()
                val user = snapshot.toObject(User::class.java)
                
                // If the document doesn't have the phone number (old users), update it
                if (user?.phoneNumber.isNullOrEmpty()) {
                    firestore.collection("users").document(uid).update("phoneNumber", cleaned).await()
                }
                
                val status = user?.status ?: "ONBOARDING"
                onSuccess(status)
            } catch (e: FirebaseAuthException) {
                errorMessage = "Login failed: ${e.localizedMessage}"
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}
